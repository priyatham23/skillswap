package com.skillswap.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Certificate {
    private String id;
    private String title;
    private String issuingOrg;
    private String issueDate;
    private String credentialUrl;
    private String proofDescription;
    private String status; // PENDING, APPROVED, REJECTED
    private String adminNotes;
    private long createdAt;

    public Certificate() {
        this.status = "PENDING";
        this.createdAt = System.currentTimeMillis();
    }

    public Certificate(String id, String title, String issuingOrg, String issueDate, String credentialUrl, String proofDescription) {
        this();
        this.id = id;
        this.title = title;
        this.issuingOrg = issuingOrg;
        this.issueDate = issueDate;
        this.credentialUrl = credentialUrl;
        this.proofDescription = proofDescription;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getIssuingOrg() { return issuingOrg; }
    public void setIssuingOrg(String issuingOrg) { this.issuingOrg = issuingOrg; }
    public String getIssueDate() { return issueDate; }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }
    public String getCredentialUrl() { return credentialUrl; }
    public void setCredentialUrl(String credentialUrl) { this.credentialUrl = credentialUrl; }
    public String getProofDescription() { return proofDescription; }
    public void setProofDescription(String proofDescription) { this.proofDescription = proofDescription; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("title", title);
        m.put("issuingOrg", issuingOrg);
        m.put("issueDate", issueDate);
        m.put("credentialUrl", credentialUrl);
        m.put("proofDescription", proofDescription);
        m.put("status", status);
        m.put("adminNotes", adminNotes);
        m.put("createdAt", createdAt);
        return m;
    }
}

