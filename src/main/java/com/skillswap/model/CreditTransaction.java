package com.skillswap.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class CreditTransaction {
    private String id;
    private String userId;
    private String transactionType; // SIGNUP_BONUS, PURCHASE, ESCROW_HOLD, ESCROW_RELEASE, ESCROW_REFUND, DONATION, POOL_GRANT
    private int amount;
    private int balanceAfter;
    private String description;
    private String referenceId;
    private long createdAt;

    public CreditTransaction() {
        this.createdAt = System.currentTimeMillis();
    }

    public CreditTransaction(String id, String userId, String transactionType, int amount,
                             int balanceAfter, String description, String referenceId) {
        this();
        this.id = id;
        this.userId = userId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.referenceId = referenceId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public int getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(int balanceAfter) { this.balanceAfter = balanceAfter; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("userId", userId);
        m.put("transactionType", transactionType);
        m.put("amount", amount);
        m.put("balanceAfter", balanceAfter);
        m.put("description", description);
        m.put("referenceId", referenceId);
        m.put("createdAt", createdAt);
        return m;
    }
}

