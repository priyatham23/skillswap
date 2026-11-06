package com.skillswap.model;

import java.util.*;

public class PayItForwardPool {
    private int poolBalance;
    private List<Map<String, Object>> donationHistory = new ArrayList<>();
    private List<Map<String, Object>> grantHistory = new ArrayList<>();

    public PayItForwardPool() {
        this.poolBalance = 500; // Seed pool with 500 community credits
    }

    public synchronized void donate(String userId, String userName, int amount, String note) {
        this.poolBalance += amount;
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("id", UUID.randomUUID().toString());
        entry.put("userId", userId);
        entry.put("userName", userName);
        entry.put("amount", amount);
        entry.put("note", note);
        entry.put("createdAt", System.currentTimeMillis());
        donationHistory.add(0, entry);
    }

    public synchronized boolean grant(String userId, String userName, int amount, String reason) {
        if (this.poolBalance < amount) {
            return false;
        }
        this.poolBalance -= amount;
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("id", UUID.randomUUID().toString());
        entry.put("userId", userId);
        entry.put("userName", userName);
        entry.put("amount", amount);
        entry.put("reason", reason);
        entry.put("createdAt", System.currentTimeMillis());
        grantHistory.add(0, entry);
        return true;
    }

    public int getPoolBalance() { return poolBalance; }
    public void setPoolBalance(int poolBalance) { this.poolBalance = poolBalance; }
    public List<Map<String, Object>> getDonationHistory() { return donationHistory; }
    public List<Map<String, Object>> getGrantHistory() { return grantHistory; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("poolBalance", poolBalance);
        m.put("totalDonations", donationHistory.size());
        m.put("totalGrants", grantHistory.size());
        m.put("recentDonations", donationHistory.subList(0, Math.min(10, donationHistory.size())));
        m.put("recentGrants", grantHistory.subList(0, Math.min(10, grantHistory.size())));
        return m;
    }
}

