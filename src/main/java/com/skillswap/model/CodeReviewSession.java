package com.skillswap.model;

import java.util.Map;

/**
 * Code Review Swaps: Submit GitHub PR or repository for thorough peer architectural & code review.
 */
public class CodeReviewSession extends Session {
    private String repoUrl;
    private String pullRequestUrl;
    private String programmingLanguage;

    public CodeReviewSession() {
        super();
        this.durationMinutes = 45;
        this.creditAmount = 15;
    }

    public CodeReviewSession(String id, String teacherId, String learnerId, String skillTopic,
                             String description, String repoUrl, String pullRequestUrl, String programmingLanguage) {
        super(id, teacherId, learnerId, skillTopic, description, "Async or 45-min Pair Review", 45, 15);
        this.repoUrl = repoUrl;
        this.pullRequestUrl = pullRequestUrl;
        this.programmingLanguage = programmingLanguage;
    }

    @Override
    public String getSessionType() {
        return "CODE_REVIEW";
    }

    @Override
    public int calculateEscrow() {
        return creditAmount;
    }

    @Override
    public void validateSessionCreation() throws IllegalArgumentException {
        if ((repoUrl == null || repoUrl.trim().isEmpty()) && (pullRequestUrl == null || pullRequestUrl.trim().isEmpty())) {
            throw new IllegalArgumentException("Repository or Pull Request URL is required for Code Review.");
        }
    }

    public String getRepoUrl() { return repoUrl; }
    public void setRepoUrl(String repoUrl) { this.repoUrl = repoUrl; }
    public String getPullRequestUrl() { return pullRequestUrl; }
    public void setPullRequestUrl(String pullRequestUrl) { this.pullRequestUrl = pullRequestUrl; }
    public String getProgrammingLanguage() { return programmingLanguage; }
    public void setProgrammingLanguage(String programmingLanguage) { this.programmingLanguage = programmingLanguage; }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = super.toMap();
        m.put("repoUrl", repoUrl);
        m.put("pullRequestUrl", pullRequestUrl);
        m.put("programmingLanguage", programmingLanguage);
        return m;
    }
}

