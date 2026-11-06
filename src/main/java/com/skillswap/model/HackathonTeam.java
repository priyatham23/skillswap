package com.skillswap.model;

import java.util.*;

public class HackathonTeam {
    private String id;
    private String organizerId;
    private String organizerName;
    private String title;
    private String hackathonName;
    private String description;
    private String lookingForSkills;
    private int maxMembers;
    private List<Map<String, String>> members = new ArrayList<>();
    private long createdAt;

    public HackathonTeam() {
        this.createdAt = System.currentTimeMillis();
        this.maxMembers = 4;
    }

    public HackathonTeam(String id, String organizerId, String organizerName, String title,
                         String hackathonName, String description, String lookingForSkills, int maxMembers) {
        this();
        this.id = id;
        this.organizerId = organizerId;
        this.organizerName = organizerName;
        this.title = title;
        this.hackathonName = hackathonName;
        this.description = description;
        this.lookingForSkills = lookingForSkills;
        this.maxMembers = maxMembers;
        Map<String, String> leader = new HashMap<>();
        leader.put("userId", organizerId);
        leader.put("name", organizerName);
        leader.put("role", "Team Lead / Organizer");
        this.members.add(leader);
    }

    public boolean addMember(String userId, String userName, String role) {
        if (members.size() >= maxMembers) {
            return false;
        }
        for (Map<String, String> m : members) {
            if (userId.equals(m.get("userId"))) return false;
        }
        Map<String, String> m = new HashMap<>();
        m.put("userId", userId);
        m.put("name", userName);
        m.put("role", role);
        members.add(m);
        return true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }
    public String getOrganizerName() { return organizerName; }
    public void setOrganizerName(String organizerName) { this.organizerName = organizerName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getHackathonName() { return hackathonName; }
    public void setHackathonName(String hackathonName) { this.hackathonName = hackathonName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLookingForSkills() { return lookingForSkills; }
    public void setLookingForSkills(String lookingForSkills) { this.lookingForSkills = lookingForSkills; }
    public int getMaxMembers() { return maxMembers; }
    public void setMaxMembers(int maxMembers) { this.maxMembers = maxMembers; }
    public List<Map<String, String>> getMembers() { return members; }
    public void setMembers(List<Map<String, String>> members) { this.members = members; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("organizerId", organizerId);
        m.put("organizerName", organizerName);
        m.put("title", title);
        m.put("hackathonName", hackathonName);
        m.put("description", description);
        m.put("lookingForSkills", lookingForSkills);
        m.put("maxMembers", maxMembers);
        m.put("currentMembers", members.size());
        m.put("members", members);
        m.put("createdAt", createdAt);
        return m;
    }
}

