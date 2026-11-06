package com.skillswap.model;

import java.util.*;

public class User {
    private String id;
    private String name;
    private String email;
    private String passwordHash;
    private String salt;
    private String avatarUrl;
    private String headline;
    private String bio;
    private String college;
    private String linkedinUrl;
    private String githubUsername;
    private String tier; // Beginner, Intermediate, Pro
    private int tierPrice; // in credits
    private int credits;
    private int escrowCredits;
    private int streakDays;
    private long lastActive;
    private String role; // USER, ADMIN
    private boolean isBanned;
    private long createdAt;
    private String availableFormats = "One-to-One, Group Session";
    private String availableTimeSlots = "Flexible / Weekdays 5-8 PM";
    private String mentorIntro = "";
    private String expertiseLevel = "Intermediate";

    private List<Education> educations = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();
    private List<Certificate> certificates = new ArrayList<>();
    private List<Skill> skills = new ArrayList<>();

    public User() {
        this.tier = "Beginner";
        this.tierPrice = 10;
        this.credits = 100; // Sign-up bonus
        this.escrowCredits = 0;
        this.streakDays = 1;
        this.lastActive = System.currentTimeMillis();
        this.role = "USER";
        this.isBanned = false;
        this.createdAt = System.currentTimeMillis();
        this.avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=250&q=80";
    }

    public User(String id, String name, String email, String passwordHash, String salt) {
        this();
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    // Business logic for Tier recalculation
    public void recalculateTier(int completedSessionsCount, double averageRating) {
        int verifiedSkillsCount = 0;
        for (Skill s : skills) {
            if ("TEACH".equalsIgnoreCase(s.getSkillType()) && s.isVerified()) {
                verifiedSkillsCount++;
            }
        }
        int approvedCertCount = 0;
        for (Certificate c : certificates) {
            if ("APPROVED".equalsIgnoreCase(c.getStatus())) {
                approvedCertCount++;
            }
        }

        // Tier formula
        if ((verifiedSkillsCount >= 3 || approvedCertCount >= 2) && completedSessionsCount >= 8 && averageRating >= 4.5) {
            this.tier = "Pro";
            this.tierPrice = 50;
        } else if ((verifiedSkillsCount >= 1 || approvedCertCount >= 1) && completedSessionsCount >= 3 && averageRating >= 4.0) {
            this.tier = "Intermediate";
            this.tierPrice = 25;
        } else {
            this.tier = "Beginner";
            this.tierPrice = 10;
        }
    }

    public boolean hasSufficientCredits(int amount) {
        return this.credits >= amount;
    }

    public void holdEscrow(int amount) {
        if (!hasSufficientCredits(amount)) {
            throw new IllegalStateException("Insufficient credits to hold in escrow.");
        }
        this.credits -= amount;
        this.escrowCredits += amount;
    }

    public void releaseEscrow(int amount) {
        if (this.escrowCredits < amount) {
            throw new IllegalStateException("Escrow balance smaller than release amount.");
        }
        this.escrowCredits -= amount;
    }

    public void refundEscrow(int amount) {
        if (this.escrowCredits < amount) {
            throw new IllegalStateException("Escrow balance smaller than refund amount.");
        }
        this.escrowCredits -= amount;
        this.credits += amount;
    }

    public void addCredits(int amount) {
        this.credits += amount;
    }

    public void deductCredits(int amount) {
        if (this.credits < amount) {
            throw new IllegalStateException("Insufficient credits.");
        }
        this.credits -= amount;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }
    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }
    public String getGithubUsername() { return githubUsername; }
    public void setGithubUsername(String githubUsername) { this.githubUsername = githubUsername; }
    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
    public int getTierPrice() { return tierPrice; }
    public void setTierPrice(int tierPrice) { this.tierPrice = tierPrice; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public int getEscrowCredits() { return escrowCredits; }
    public void setEscrowCredits(int escrowCredits) { this.escrowCredits = escrowCredits; }
    public int getStreakDays() { return streakDays; }
    public void setStreakDays(int streakDays) { this.streakDays = streakDays; }
    public long getLastActive() { return lastActive; }
    public void setLastActive(long lastActive) { this.lastActive = lastActive; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isBanned() { return isBanned; }
    public void setBanned(boolean banned) { isBanned = banned; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public String getAvailableFormats() { return availableFormats; }
    public void setAvailableFormats(String availableFormats) { this.availableFormats = availableFormats; }
    public String getAvailableTimeSlots() { return availableTimeSlots; }
    public void setAvailableTimeSlots(String availableTimeSlots) { this.availableTimeSlots = availableTimeSlots; }
    public String getMentorIntro() { return mentorIntro; }
    public void setMentorIntro(String mentorIntro) { this.mentorIntro = mentorIntro; }
    public String getExpertiseLevel() { return expertiseLevel; }
    public void setExpertiseLevel(String expertiseLevel) { this.expertiseLevel = expertiseLevel; }

    public List<Education> getEducations() { return educations; }
    public void setEducations(List<Education> educations) { this.educations = educations; }
    public List<Project> getProjects() { return projects; }
    public void setProjects(List<Project> projects) { this.projects = projects; }
    public List<Certificate> getCertificates() { return certificates; }
    public void setCertificates(List<Certificate> certificates) { this.certificates = certificates; }
    public List<Skill> getSkills() { return skills; }
    public void setSkills(List<Skill> skills) { this.skills = skills; }

    public Map<String, Object> toMap(boolean includePrivate) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("email", email);
        if (includePrivate) {
            map.put("role", role);
            map.put("credits", credits);
            map.put("escrowCredits", escrowCredits);
        }
        map.put("avatarUrl", avatarUrl);
        map.put("headline", headline);
        map.put("bio", bio);
        map.put("college", college);
        map.put("linkedinUrl", linkedinUrl);
        map.put("githubUsername", githubUsername);
        map.put("tier", tier);
        map.put("tierPrice", tierPrice);
        map.put("streakDays", streakDays);
        map.put("lastActive", lastActive);
        map.put("isBanned", isBanned);
        map.put("createdAt", createdAt);
        map.put("availableFormats", availableFormats != null ? availableFormats : "One-to-One, Group Session");
        map.put("availableTimeSlots", availableTimeSlots != null ? availableTimeSlots : "Flexible / Weekdays 5-8 PM");
        map.put("mentorIntro", (mentorIntro != null && !mentorIntro.isEmpty()) ? mentorIntro : (bio != null && !bio.isEmpty() ? bio : (headline != null ? headline : "Campus Peer Mentor")));
        map.put("expertiseLevel", expertiseLevel != null ? expertiseLevel : (tier != null ? tier : "Intermediate"));

        List<Map<String, Object>> eduList = new ArrayList<>();
        for (Education e : educations) eduList.add(e.toMap());
        map.put("educations", eduList);

        List<Map<String, Object>> projList = new ArrayList<>();
        for (Project p : projects) projList.add(p.toMap());
        map.put("projects", projList);

        List<Map<String, Object>> certList = new ArrayList<>();
        for (Certificate c : certificates) certList.add(c.toMap());
        map.put("certificates", certList);

        List<Map<String, Object>> skillList = new ArrayList<>();
        for (Skill s : skills) skillList.add(s.toMap());
        map.put("skills", skillList);

        return map;
    }
}

