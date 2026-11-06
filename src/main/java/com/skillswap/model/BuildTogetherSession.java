package com.skillswap.model;

import java.util.Map;

/**
 * Build Together: Collaborative project build swap linked to a GitHub repository.
 */
public class BuildTogetherSession extends Session {
    private String projectRepoUrl;
    private String techStack;
    private String milestoneGoal;

    public BuildTogetherSession() {
        super();
        this.durationMinutes = 120;
        this.creditAmount = 25;
    }

    public BuildTogetherSession(String id, String teacherId, String learnerId, String skillTopic,
                               String description, String scheduledTime, String projectRepoUrl,
                               String techStack, String milestoneGoal) {
        super(id, teacherId, learnerId, skillTopic, description, scheduledTime, 120, 25);
        this.projectRepoUrl = projectRepoUrl;
        this.techStack = techStack;
        this.milestoneGoal = milestoneGoal;
    }

    @Override
    public String getSessionType() {
        return "BUILD_TOGETHER";
    }

    @Override
    public int calculateEscrow() {
        return creditAmount;
    }

    @Override
    public void validateSessionCreation() throws IllegalArgumentException {
        if (projectRepoUrl == null || projectRepoUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("GitHub repository URL is required for Build Together session.");
        }
    }

    public String getProjectRepoUrl() { return projectRepoUrl; }
    public void setProjectRepoUrl(String projectRepoUrl) { this.projectRepoUrl = projectRepoUrl; }
    public String getTechStack() { return techStack; }
    public void setTechStack(String techStack) { this.techStack = techStack; }
    public String getMilestoneGoal() { return milestoneGoal; }
    public void setMilestoneGoal(String milestoneGoal) { this.milestoneGoal = milestoneGoal; }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = super.toMap();
        m.put("projectRepoUrl", projectRepoUrl);
        m.put("techStack", techStack);
        m.put("milestoneGoal", milestoneGoal);
        return m;
    }
}

