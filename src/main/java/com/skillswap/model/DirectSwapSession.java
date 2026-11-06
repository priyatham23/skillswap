package com.skillswap.model;

import java.util.Map;

/**
 * Direct Swap Session: A reciprocal exchange where Learner swaps a skill with Teacher.
 */
public class DirectSwapSession extends Session {
    private String reciprocalSkill;

    public DirectSwapSession() {
        super();
    }

    public DirectSwapSession(String id, String teacherId, String learnerId, String skillTopic,
                             String description, String scheduledTime, int durationMinutes,
                             int creditAmount, String reciprocalSkill) {
        super(id, teacherId, learnerId, skillTopic, description, scheduledTime, durationMinutes, creditAmount);
        this.reciprocalSkill = reciprocalSkill;
    }

    @Override
    public String getSessionType() {
        return "DIRECT_SWAP";
    }

    @Override
    public int calculateEscrow() {
        return creditAmount;
    }

    @Override
    public void validateSessionCreation() throws IllegalArgumentException {
        if (teacherId == null || learnerId == null) {
            throw new IllegalArgumentException("Teacher and Learner IDs are required.");
        }
        if (teacherId.equals(learnerId)) {
            throw new IllegalArgumentException("Cannot swap with yourself.");
        }
    }

    public String getReciprocalSkill() { return reciprocalSkill; }
    public void setReciprocalSkill(String reciprocalSkill) { this.reciprocalSkill = reciprocalSkill; }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = super.toMap();
        m.put("reciprocalSkill", reciprocalSkill);
        return m;
    }
}

