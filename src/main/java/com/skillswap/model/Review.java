package com.skillswap.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Review {
    private String id;
    private String sessionId;
    private String teacherId;
    private String learnerId;
    private String learnerName;
    private int rating; // 1 to 5
    private String reviewText;
    private long createdAt;

    public Review() {
        this.createdAt = System.currentTimeMillis();
    }

    public Review(String id, String sessionId, String teacherId, String learnerId, String learnerName, int rating, String reviewText) {
        this();
        this.id = id;
        this.sessionId = sessionId;
        this.teacherId = teacherId;
        this.learnerId = learnerId;
        this.learnerName = learnerName;
        this.rating = rating;
        this.reviewText = reviewText;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }
    public String getLearnerId() { return learnerId; }
    public void setLearnerId(String learnerId) { this.learnerId = learnerId; }
    public String getLearnerName() { return learnerName; }
    public void setLearnerName(String learnerName) { this.learnerName = learnerName; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("sessionId", sessionId);
        m.put("teacherId", teacherId);
        m.put("learnerId", learnerId);
        m.put("learnerName", learnerName);
        m.put("rating", rating);
        m.put("reviewText", reviewText);
        m.put("createdAt", createdAt);
        return m;
    }
}

