package com.skillswap.model;

import java.util.Map;

/**
 * Doubt Sprint: 15-minute quick help session for 1 to 2 credits.
 * Designed for rapid debugging, query resolution, and quick unblocking.
 */
public class DoubtSprintSession extends Session {
    private String urgencyLevel; // HIGH, MEDIUM, LOW
    private String errorSnippet;

    public DoubtSprintSession() {
        super();
        this.durationMinutes = 15;
        this.creditAmount = 2; // Standard 2 credits for Doubt Sprint
    }

    public DoubtSprintSession(String id, String teacherId, String learnerId, String skillTopic,
                              String description, String urgencyLevel, String errorSnippet) {
        super(id, teacherId, learnerId, skillTopic, description, "Immediate / Next available", 15, 2);
        this.urgencyLevel = urgencyLevel;
        this.errorSnippet = errorSnippet;
    }

    @Override
    public String getSessionType() {
        return "DOUBT_SPRINT";
    }

    @Override
    public int calculateEscrow() {
        return Math.max(1, Math.min(2, creditAmount));
    }

    @Override
    public void validateSessionCreation() throws IllegalArgumentException {
        if (skillTopic == null || skillTopic.trim().isEmpty()) {
            throw new IllegalArgumentException("Doubt topic is required for Doubt Sprint.");
        }
    }

    public String getUrgencyLevel() { return urgencyLevel; }
    public void setUrgencyLevel(String urgencyLevel) { this.urgencyLevel = urgencyLevel; }
    public String getErrorSnippet() { return errorSnippet; }
    public void setErrorSnippet(String errorSnippet) { this.errorSnippet = errorSnippet; }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = super.toMap();
        m.put("urgencyLevel", urgencyLevel);
        m.put("errorSnippet", errorSnippet);
        return m;
    }
}

