package com.skillswap.model;

import java.util.*;

/**
 * Abstract Base Class for Sessions.
 * Demonstrates Object-Oriented Polymorphism and Template Method patterns.
 */
public abstract class Session {
    protected String id;
    protected String teacherId;
    protected String learnerId;
    protected String skillTopic;
    protected String description;
    protected String scheduledTime;
    protected int durationMinutes;
    protected int creditAmount;
    protected SessionStatus status;
    protected String meetingLink;
    protected long createdAt;

    public Session() {
        this.status = SessionStatus.REQUESTED;
        this.createdAt = System.currentTimeMillis();
    }

    public Session(String id, String teacherId, String learnerId, String skillTopic, String description,
                   String scheduledTime, int durationMinutes, int creditAmount) {
        this();
        this.id = id;
        this.teacherId = teacherId;
        this.learnerId = learnerId;
        this.skillTopic = skillTopic;
        this.description = description;
        this.scheduledTime = scheduledTime;
        this.durationMinutes = durationMinutes;
        this.creditAmount = creditAmount;
        this.meetingLink = "https://meet.jit.si/SkillSwap-" + id.substring(0, Math.min(8, id.length()));
    }

    // Abstract polymorphic methods
    public abstract String getSessionType();
    public abstract int calculateEscrow();
    public abstract void validateSessionCreation() throws IllegalArgumentException;

    // Transition state
    public void accept() {
        if (this.status != SessionStatus.REQUESTED) {
            throw new IllegalStateException("Only requested sessions can be accepted.");
        }
        this.status = SessionStatus.ACCEPTED;
    }

    public void reject() {
        if (this.status != SessionStatus.REQUESTED) {
            throw new IllegalStateException("Only requested sessions can be rejected.");
        }
        this.status = SessionStatus.REJECTED;
    }

    public void cancel() {
        if (this.status == SessionStatus.COMPLETED) {
            throw new IllegalStateException("Completed sessions cannot be cancelled.");
        }
        this.status = SessionStatus.CANCELLED;
    }

    public void complete() {
        if (this.status != SessionStatus.ACCEPTED) {
            throw new IllegalStateException("Only accepted sessions can be marked completed.");
        }
        this.status = SessionStatus.COMPLETED;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }
    public String getLearnerId() { return learnerId; }
    public void setLearnerId(String learnerId) { this.learnerId = learnerId; }
    public String getSkillTopic() { return skillTopic; }
    public void setSkillTopic(String skillTopic) { this.skillTopic = skillTopic; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public int getCreditAmount() { return creditAmount; }
    public void setCreditAmount(int creditAmount) { this.creditAmount = creditAmount; }
    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }
    public String getMeetingLink() { return meetingLink; }
    public void setMeetingLink(String meetingLink) { this.meetingLink = meetingLink; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("sessionType", getSessionType());
        map.put("teacherId", teacherId);
        map.put("learnerId", learnerId);
        map.put("skillTopic", skillTopic);
        map.put("description", description);
        map.put("scheduledTime", scheduledTime);
        map.put("durationMinutes", durationMinutes);
        map.put("creditAmount", creditAmount);
        map.put("status", status.name());
        map.put("meetingLink", meetingLink);
        map.put("createdAt", createdAt);
        return map;
    }
}

