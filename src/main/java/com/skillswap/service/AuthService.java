package com.skillswap.service;

import com.skillswap.model.CreditTransaction;
import com.skillswap.model.User;
import com.skillswap.repository.DataRepository;
import com.skillswap.util.SecurityUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthService {
    private final DataRepository repository;
    private final Map<String, String> activeTokens = new ConcurrentHashMap<>(); // token -> userId

    public AuthService(DataRepository repository) {
        this.repository = repository;
    }

    public synchronized User register(String name, String email, String password, String college) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Email and password are required.");
        }
        if (repository.findUserByEmail(email) != null) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        String salt = SecurityUtils.generateSalt();
        String hash = SecurityUtils.hashPassword(password, salt);
        String userId = "user-" + SecurityUtils.generateId().substring(0, 8);

        User user = new User(userId, name, email.trim().toLowerCase(), hash, salt);
        if (college != null && !college.trim().isEmpty()) {
            user.setCollege(college.trim());
        }
        user.setCredits(100); // 100 Sign-up Welcome Bonus Credits!

        repository.saveUser(user);

        // Record signup bonus in credit ledger
        repository.logCreditTransaction(new CreditTransaction(
            SecurityUtils.generateId(),
            user.getId(),
            "SIGNUP_BONUS",
            100,
            100,
            "Welcome Sign-Up Bonus Credits",
            "WELCOME"
        ));

        return user;
    }

    public User login(String email, String password) {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email and password are required.");
        }
        User user = repository.findUserByEmail(email.trim().toLowerCase());
        if (user == null) {
            throw new IllegalArgumentException("Invalid email or password.");
        }
        if (user.isBanned()) {
            throw new IllegalStateException("Your account has been suspended by campus admin.");
        }

        if (!SecurityUtils.verifyPassword(password, user.getPasswordHash(), user.getSalt())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        // Update last active
        user.setLastActive(System.currentTimeMillis());
        repository.saveUser(user);
        return user;
    }

    public String createSessionToken(User user) {
        String token = SecurityUtils.generateToken();
        activeTokens.put(token, user.getId());
        return token;
    }

    public User authenticate(String token) {
        if (token == null || !activeTokens.containsKey(token)) {
            return null;
        }
        String userId = activeTokens.get(token);
        return repository.findUserById(userId);
    }

    public void logout(String token) {
        if (token != null) {
            activeTokens.remove(token);
        }
    }
}

