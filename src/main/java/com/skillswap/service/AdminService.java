package com.skillswap.service;

import com.skillswap.model.*;
import com.skillswap.repository.DataRepository;
import com.skillswap.util.SecurityUtils;

import java.util.*;

public class AdminService {
    private final DataRepository repository;

    public AdminService(DataRepository repository) {
        this.repository = repository;
    }

    private void verifyAdmin(String adminUserId) {
        User admin = repository.findUserById(adminUserId);
        if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
            throw new SecurityException("Administrative privileges required.");
        }
    }

    public List<Map<String, Object>> getAllUsers(String adminUserId) {
        verifyAdmin(adminUserId);
        List<User> users = repository.findAllUsers();
        List<Map<String, Object>> res = new ArrayList<>();
        for (User u : users) {
            res.add(u.toMap(true));
        }
        return res;
    }

    public boolean toggleUserBan(String adminUserId, String targetUserId) {
        verifyAdmin(adminUserId);
        User target = repository.findUserById(targetUserId);
        if (target == null) throw new IllegalArgumentException("Target user not found.");
        if ("ADMIN".equalsIgnoreCase(target.getRole())) {
            throw new IllegalArgumentException("Cannot ban an administrator.");
        }

        target.setBanned(!target.isBanned());
        repository.saveUser(target);
        return target.isBanned();
    }

    public List<Map<String, Object>> getPendingCertificates(String adminUserId) {
        verifyAdmin(adminUserId);
        List<Map<String, Object>> pending = new ArrayList<>();
        for (User u : repository.findAllUsers()) {
            for (Certificate c : u.getCertificates()) {
                if ("PENDING".equalsIgnoreCase(c.getStatus())) {
                    Map<String, Object> item = c.toMap();
                    item.put("userId", u.getId());
                    item.put("userName", u.getName());
                    item.put("userCollege", u.getCollege());
                    pending.add(item);
                }
            }
        }
        return pending;
    }

    public void reviewCertificate(String adminUserId, String certId, String status, String adminNotes) {
        verifyAdmin(adminUserId);
        if (!"APPROVED".equalsIgnoreCase(status) && !"REJECTED".equalsIgnoreCase(status)) {
            throw new IllegalArgumentException("Status must be APPROVED or REJECTED.");
        }

        for (User u : repository.findAllUsers()) {
            for (Certificate c : u.getCertificates()) {
                if (c.getId().equals(certId)) {
                    c.setStatus(status.toUpperCase());
                    c.setAdminNotes(adminNotes);

                    if ("APPROVED".equalsIgnoreCase(status)) {
                        // Recalculate tier
                        List<Session> sessions = repository.findSessionsByUserId(u.getId());
                        int completed = 0;
                        for (Session s : sessions) {
                            if (s.getStatus() == SessionStatus.COMPLETED && u.getId().equals(s.getTeacherId())) {
                                completed++;
                            }
                        }
                        List<Review> revs = repository.findReviewsByTeacherId(u.getId());
                        double avg = 5.0;
                        if (!revs.isEmpty()) {
                            double sum = 0;
                            for (Review r : revs) sum += r.getRating();
                            avg = sum / revs.size();
                        }
                        u.recalculateTier(completed, avg);
                    }

                    repository.saveUser(u);

                    // Notify user
                    repository.saveNotification(new Notification(
                        SecurityUtils.generateId(),
                        u.getId(),
                        "Certificate Review Update: " + status.toUpperCase(),
                        "Your certificate '" + c.getTitle() + "' was " + status.toLowerCase() + " by campus admin. " + (adminNotes != null ? adminNotes : ""),
                        "#profile"
                    ));
                    return;
                }
            }
        }
        throw new IllegalArgumentException("Certificate not found.");
    }

    public Map<String, Object> getPlatformStats(String adminUserId) {
        verifyAdmin(adminUserId);
        Map<String, Object> stats = new LinkedHashMap<>();

        List<User> users = repository.findAllUsers();
        List<Session> sessions = repository.findAllSessions();

        int totalUsers = users.size();
        int activeTeachers = 0;
        int totalCirculatingCredits = 0;
        int totalEscrowHeld = 0;

        for (User u : users) {
            totalCirculatingCredits += u.getCredits();
            totalEscrowHeld += u.getEscrowCredits();
            boolean teaches = false;
            for (Skill s : u.getSkills()) {
                if ("TEACH".equalsIgnoreCase(s.getSkillType())) teaches = true;
            }
            if (teaches) activeTeachers++;
        }

        int completedSessions = 0;
        int activeSessions = 0;
        for (Session s : sessions) {
            if (s.getStatus() == SessionStatus.COMPLETED) completedSessions++;
            else if (s.getStatus() == SessionStatus.ACCEPTED) activeSessions++;
        }

        stats.put("totalUsers", totalUsers);
        stats.put("activeTeachers", activeTeachers);
        stats.put("totalCirculatingCredits", totalCirculatingCredits);
        stats.put("totalEscrowHeld", totalEscrowHeld);
        stats.put("totalSessions", sessions.size());
        stats.put("completedSessions", completedSessions);
        stats.put("activeSessions", activeSessions);
        stats.put("mySqlConnected", repository.isMySqlConnected());
        stats.put("databaseStatus", repository.getDatabaseStatus());
        stats.put("poolBalance", repository.getPayItForwardPool().getPoolBalance());

        return stats;
    }
}

