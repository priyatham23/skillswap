package com.skillswap.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Skill {
    private String id;
    private String skillName;
    private String skillType; // TEACH or LEARN
    private String proficiencyLevel; // Beginner, Intermediate, Advanced, Expert
    private boolean isVerified;
    private int quizScore; // 0 to 100

    public Skill() {}

    public Skill(String id, String skillName, String skillType, String proficiencyLevel, boolean isVerified, int quizScore) {
        this.id = id;
        this.skillName = skillName;
        this.skillType = skillType;
        this.proficiencyLevel = proficiencyLevel;
        this.isVerified = isVerified;
        this.quizScore = quizScore;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public String getSkillType() { return skillType; }
    public void setSkillType(String skillType) { this.skillType = skillType; }
    public String getProficiencyLevel() { return proficiencyLevel; }
    public void setProficiencyLevel(String proficiencyLevel) { this.proficiencyLevel = proficiencyLevel; }
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
    public int getQuizScore() { return quizScore; }
    public void setQuizScore(int quizScore) { this.quizScore = quizScore; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("skillName", skillName);
        m.put("skillType", skillType);
        m.put("proficiencyLevel", proficiencyLevel);
        m.put("isVerified", isVerified);
        m.put("quizScore", quizScore);
        return m;
    }
}

