package com.skillswap.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Notification {
    private String id;
    private String userId;
    private String title;
    private String message;
    private String actionUrl;
    private boolean isRead;
    private long createdAt;

    public Notification() {
        this.createdAt = System.currentTimeMillis();
        this.isRead = false;
    }

    public Notification(String id, String userId, String title, String message, String actionUrl) {
        this();
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.actionUrl = actionUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getActionUrl() { return actionUrl; }
    public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("userId", userId);
        m.put("title", title);
        m.put("message", message);
        m.put("actionUrl", actionUrl);
        m.put("isRead", isRead);
        m.put("createdAt", createdAt);
        return m;
    }
}

