package com.skillswap.model;

import java.util.*;

public class QuizQuestion {
    private String id;
    private String skillName;
    private String question;
    private List<String> options;
    private int correctOptionIndex;
    private String explanation;

    public QuizQuestion() {
        this.options = new ArrayList<>();
    }

    public QuizQuestion(String id, String skillName, String question, List<String> options, int correctOptionIndex, String explanation) {
        this.id = id;
        this.skillName = skillName;
        this.question = question;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
        this.explanation = explanation;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    public int getCorrectOptionIndex() { return correctOptionIndex; }
    public void setCorrectOptionIndex(int correctOptionIndex) { this.correctOptionIndex = correctOptionIndex; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public Map<String, Object> toMap(boolean includeAnswer) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("skillName", skillName);
        m.put("question", question);
        m.put("options", options);
        if (includeAnswer) {
            m.put("correctOptionIndex", correctOptionIndex);
            m.put("explanation", explanation);
        }
        return m;
    }
}

