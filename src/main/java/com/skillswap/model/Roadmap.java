package com.skillswap.model;

import java.util.*;

public class Roadmap {
    private String id;
    private String title;
    private String role;
    private String description;
    private String duration;
    private String icon;
    private List<RoadmapStep> steps = new ArrayList<>();

    public Roadmap() {}

    public Roadmap(String id, String title, String role, String description, String duration, String icon) {
        this.id = id;
        this.title = title;
        this.role = role;
        this.description = description;
        this.duration = duration;
        this.icon = icon;
    }

    public static class RoadmapStep {
        private String stepId;
        private String title;
        private String description;
        private String skillToSwap;
        private List<String> resources;

        public RoadmapStep() {
            this.resources = new ArrayList<>();
        }

        public RoadmapStep(String stepId, String title, String description, String skillToSwap, List<String> resources) {
            this.stepId = stepId;
            this.title = title;
            this.description = description;
            this.skillToSwap = skillToSwap;
            this.resources = resources;
        }

        public String getStepId() { return stepId; }
        public void setStepId(String stepId) { this.stepId = stepId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getSkillToSwap() { return skillToSwap; }
        public void setSkillToSwap(String skillToSwap) { this.skillToSwap = skillToSwap; }
        public List<String> getResources() { return resources; }
        public void setResources(List<String> resources) { this.resources = resources; }

        public Map<String, Object> toMap() {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("stepId", stepId);
            m.put("title", title);
            m.put("description", description);
            m.put("skillToSwap", skillToSwap);
            m.put("resources", resources);
            return m;
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public List<RoadmapStep> getSteps() { return steps; }
    public void setSteps(List<RoadmapStep> steps) { this.steps = steps; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("title", title);
        m.put("role", role);
        m.put("description", description);
        m.put("duration", duration);
        m.put("icon", icon);
        List<Map<String, Object>> stepMaps = new ArrayList<>();
        for (RoadmapStep s : steps) stepMaps.add(s.toMap());
        m.put("steps", stepMaps);
        return m;
    }
}

