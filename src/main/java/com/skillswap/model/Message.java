package com.skillswap.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Message {
    private String id;
    private String senderId;
    private String senderName;
    private String receiverId;
    private String content;
    private boolean isRead;
    private long createdAt;

    public Message() {
        this.createdAt = System.currentTimeMillis();
    }

    public Message(String id, String senderId, String senderName, String receiverId, String content) {
        this();
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.content = content;
        this.isRead = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("senderId", senderId);
        m.put("senderName", senderName);
        m.put("receiverId", receiverId);
        m.put("content", content);
        m.put("isRead", isRead);
        m.put("createdAt", createdAt);
        return m;
    }
}

