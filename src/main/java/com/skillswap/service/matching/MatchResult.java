package com.skillswap.service.matching;

import java.util.*;

public class MatchResult {
    private String matchType; // DIRECT or CHAIN
    private int score; // 0 to 100
    private String reason;
    private List<Map<String, Object>> participants;
    private Map<String, Object> details;

    public MatchResult() {
        this.participants = new ArrayList<>();
        this.details = new HashMap<>();
    }

    public MatchResult(String matchType, int score, String reason) {
        this();
        this.matchType = matchType;
        this.score = score;
        this.reason = reason;
    }

    public String getMatchType() { return matchType; }
    public void setMatchType(String matchType) { this.matchType = matchType; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public List<Map<String, Object>> getParticipants() { return participants; }
    public void setParticipants(List<Map<String, Object>> participants) { this.participants = participants; }
    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("matchType", matchType);
        m.put("score", score);
        m.put("reason", reason);
        m.put("participants", participants);
        m.put("details", details);
        return m;
    }
}

