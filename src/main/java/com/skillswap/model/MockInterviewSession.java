package com.skillswap.model;

import java.util.Map;

/**
 * Mock Interview Exchange: Technical & Behavioral interview practice with evaluation rubric.
 */
public class MockInterviewSession extends Session {
    private String interviewType; // Technical DSA, Frontend, System Design, Behavioral
    private String targetCompany;
    private String targetRole;
    private String evaluationRubric;

    public MockInterviewSession() {
        super();
        this.durationMinutes = 60;
        this.creditAmount = 20;
    }

    public MockInterviewSession(String id, String teacherId, String learnerId, String skillTopic,
                                String description, String scheduledTime, String interviewType,
                                String targetCompany, String targetRole) {
        super(id, teacherId, learnerId, skillTopic, description, scheduledTime, 60, 20);
        this.interviewType = interviewType;
        this.targetCompany = targetCompany;
        this.targetRole = targetRole;
        this.evaluationRubric = "Problem Solving (1-5), Code Quality (1-5), Communication (1-5)";
    }

    @Override
    public String getSessionType() {
        return "MOCK_INTERVIEW";
    }

    @Override
    public int calculateEscrow() {
        return creditAmount;
    }

    @Override
    public void validateSessionCreation() throws IllegalArgumentException {
        if (targetRole == null || targetRole.trim().isEmpty()) {
            throw new IllegalArgumentException("Target role must be specified for mock interview.");
        }
    }

    public String getInterviewType() { return interviewType; }
    public void setInterviewType(String interviewType) { this.interviewType = interviewType; }
    public String getTargetCompany() { return targetCompany; }
    public void setTargetCompany(String targetCompany) { this.targetCompany = targetCompany; }
    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
    public String getEvaluationRubric() { return evaluationRubric; }
    public void setEvaluationRubric(String evaluationRubric) { this.evaluationRubric = evaluationRubric; }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = super.toMap();
        m.put("interviewType", interviewType);
        m.put("targetCompany", targetCompany);
        m.put("targetRole", targetRole);
        m.put("evaluationRubric", evaluationRubric);
        return m;
    }
}

