package com.skillswap.model;

import java.util.*;

public class KnowledgePost {
    private String id;
    private String authorId;
    private String authorName;
    private String title;
    private String category;
    private String tags;
    private String content;
    private int upvotes;
    private Set<String> upvotedBy = new HashSet<>();
    private List<Map<String, Object>> answers = new ArrayList<>();
    private long createdAt;

    public KnowledgePost() {
        this.createdAt = System.currentTimeMillis();
    }

    public KnowledgePost(String id, String authorId, String authorName, String title,
                         String category, String tags, String content) {
        this();
        this.id = id;
        this.authorId = authorId;
        this.authorName = authorName;
        this.title = title;
        this.category = category;
        this.tags = tags;
        this.content = content;
        this.upvotes = 0;
    }

    public boolean toggleUpvote(String userId) {
        if (upvotedBy.contains(userId)) {
            upvotedBy.remove(userId);
            upvotes = Math.max(0, upvotes - 1);
            return false;
        } else {
            upvotedBy.add(userId);
            upvotes++;
            return true;
        }
    }

    public void addAnswer(String responderId, String responderName, String answerText) {
        Map<String, Object> ans = new LinkedHashMap<>();
        ans.put("id", UUID.randomUUID().toString());
        ans.put("responderId", responderId);
        ans.put("responderName", responderName);
        ans.put("answerText", answerText);
        ans.put("createdAt", System.currentTimeMillis());
        this.answers.add(ans);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getUpvotes() { return upvotes; }
    public void setUpvotes(int upvotes) { this.upvotes = upvotes; }
    public Set<String> getUpvotedBy() { return upvotedBy; }
    public void setUpvotedBy(Set<String> upvotedBy) { this.upvotedBy = upvotedBy; }
    public List<Map<String, Object>> getAnswers() { return answers; }
    public void setAnswers(List<Map<String, Object>> answers) { this.answers = answers; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("authorId", authorId);
        m.put("authorName", authorName);
        m.put("title", title);
        m.put("category", category);
        m.put("tags", tags);
        m.put("content", content);
        m.put("upvotes", upvotes);
        m.put("answersCount", answers.size());
        m.put("answers", answers);
        m.put("createdAt", createdAt);
        return m;
    }
}

