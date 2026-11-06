package com.skillswap.service;

import com.skillswap.model.CreditTransaction;
import com.skillswap.model.PayItForwardPool;
import com.skillswap.model.User;
import com.skillswap.repository.DataRepository;
import com.skillswap.util.SecurityUtils;

import java.util.*;

public class CreditService {
    private final DataRepository repository;

    public CreditService(DataRepository repository) {
        this.repository = repository;
    }

    public synchronized Map<String, Object> buyCredits(String userId, int amount, String paymentMethod, String txRef) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive.");
        }
        User user = repository.findUserById(userId);
        if (user == null) throw new IllegalArgumentException("User not found.");

        user.addCredits(amount);
        repository.saveUser(user);

        String ref = (txRef != null && !txRef.isEmpty()) ? txRef : "PAY-" + SecurityUtils.generateId().substring(0, 8).toUpperCase();
        CreditTransaction txn = new CreditTransaction(
            SecurityUtils.generateId(),
            userId,
            "PURCHASE",
            amount,
            user.getCredits(),
            "Simulated Payment (" + (paymentMethod != null ? paymentMethod : "UPI") + ") Demo Recharge",
            ref
        );
        repository.logCreditTransaction(txn);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("newBalance", user.getCredits());
        res.put("amountAdded", amount);
        res.put("transaction", txn.toMap());
        return res;
    }

    public synchronized void holdEscrow(String userId, int amount, String referenceId) {
        User user = repository.findUserById(userId);
        if (user == null) throw new IllegalArgumentException("User not found.");

        user.holdEscrow(amount);
        repository.saveUser(user);

        CreditTransaction txn = new CreditTransaction(
            SecurityUtils.generateId(),
            userId,
            "ESCROW_HOLD",
            -amount,
            user.getCredits(),
            "Credits placed into escrow guarantee for session booking",
            referenceId
        );
        repository.logCreditTransaction(txn);
    }

    public synchronized void releaseEscrow(String learnerId, String teacherId, int amount, String referenceId) {
        User learner = repository.findUserById(learnerId);
        User teacher = repository.findUserById(teacherId);

        if (learner != null) {
            learner.releaseEscrow(amount);
            repository.saveUser(learner);
            CreditTransaction tLearner = new CreditTransaction(
                SecurityUtils.generateId(),
                learnerId,
                "ESCROW_RELEASE",
                0,
                learner.getCredits(),
                "Escrow funds successfully released to teacher for completed session",
                referenceId
            );
            repository.logCreditTransaction(tLearner);
        }

        if (teacher != null) {
            teacher.addCredits(amount);
            repository.saveUser(teacher);
            CreditTransaction tTeacher = new CreditTransaction(
                SecurityUtils.generateId(),
                teacherId,
                "TEACHING_EARNING",
                amount,
                teacher.getCredits(),
                "Earned teaching credits for completed session",
                referenceId
            );
            repository.logCreditTransaction(tTeacher);
        }
    }

    public synchronized void refundEscrow(String learnerId, int amount, String referenceId) {
        User learner = repository.findUserById(learnerId);
        if (learner == null) return;

        learner.refundEscrow(amount);
        repository.saveUser(learner);

        CreditTransaction txn = new CreditTransaction(
            SecurityUtils.generateId(),
            learnerId,
            "ESCROW_REFUND",
            amount,
            learner.getCredits(),
            "Escrow refunded due to session cancellation / rejection",
            referenceId
        );
        repository.logCreditTransaction(txn);
    }

    public synchronized void donateToPool(String userId, int amount, String note) {
        User user = repository.findUserById(userId);
        if (user == null) throw new IllegalArgumentException("User not found.");
        if (!user.hasSufficientCredits(amount)) {
            throw new IllegalStateException("Insufficient credit balance to donate.");
        }

        user.deductCredits(amount);
        repository.saveUser(user);

        PayItForwardPool pool = repository.getPayItForwardPool();
        pool.donate(userId, user.getName(), amount, note);

        CreditTransaction txn = new CreditTransaction(
            SecurityUtils.generateId(),
            userId,
            "DONATION",
            -amount,
            user.getCredits(),
            "Donated credits to Pay It Forward campus learning pool",
            "POOL"
        );
        repository.logCreditTransaction(txn);
    }

    public synchronized boolean claimFromPool(String userId, int amount, String reason) {
        User user = repository.findUserById(userId);
        if (user == null) throw new IllegalArgumentException("User not found.");

        PayItForwardPool pool = repository.getPayItForwardPool();
        boolean ok = pool.grant(userId, user.getName(), amount, reason);
        if (ok) {
            user.addCredits(amount);
            repository.saveUser(user);

            CreditTransaction txn = new CreditTransaction(
                SecurityUtils.generateId(),
                userId,
                "POOL_GRANT",
                amount,
                user.getCredits(),
                "Received community learning assistance grant from Pay It Forward pool",
                "POOL-GRANT"
            );
            repository.logCreditTransaction(txn);
            return true;
        }
        return false;
    }

    public List<CreditTransaction> getLedger(String userId) {
        return repository.findTransactionsByUserId(userId);
    }
}

