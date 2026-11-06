package com.skillswap.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Project {
    private String id;
    private String title;
    private String description;
    private String githubRepoUrl;
    private String liveUrl;
    private String tags;
    private int stars;
    private boolean isImported;

    public Project() {}

    public Project(String id, String title, String description, String githubRepoUrl, String liveUrl, String tags, int stars, boolean isImported) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.githubRepoUrl = githubRepoUrl;
        this.liveUrl = liveUrl;
        this.tags = tags;
        this.stars = stars;
        this.isImported = isImported;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getGithubRepoUrl() { return githubRepoUrl; }
    public void setGithubRepoUrl(String githubRepoUrl) { this.githubRepoUrl = githubRepoUrl; }
    public String getLiveUrl() { return liveUrl; }
    public void setLiveUrl(String liveUrl) { this.liveUrl = liveUrl; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }
    public boolean isImported() { return isImported; }
    public void setImported(boolean imported) { isImported = imported; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("title", title);
        m.put("description", description);
        m.put("githubRepoUrl", githubRepoUrl);
        m.put("liveUrl", liveUrl);
        m.put("tags", tags);
        m.put("stars", stars);
        m.put("isImported", isImported);
        return m;
    }
}

