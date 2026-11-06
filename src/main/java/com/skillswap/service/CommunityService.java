package com.skillswap.service;

import com.skillswap.model.*;
import com.skillswap.repository.DataRepository;
import com.skillswap.util.SecurityUtils;

import java.util.*;

public class CommunityService {
    private final DataRepository repository;

    public CommunityService(DataRepository repository) {
        this.repository = repository;
    }

    public Message sendMessage(String senderId, String receiverId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty.");
        }
        User sender = repository.findUserById(senderId);
        String senderName = sender != null ? sender.getName() : "Peer";

        Message msg = new Message(SecurityUtils.generateId(), senderId, senderName, receiverId, content.trim());
        repository.saveMessage(msg);

        // Notify recipient
        repository.saveNotification(new Notification(
            SecurityUtils.generateId(),
            receiverId,
            "New Message from " + senderName,
            content.length() > 50 ? content.substring(0, 50) + "..." : content,
            "#chat"
        ));

        return msg;
    }

    public List<Message> getConversation(String u1, String u2) {
        return repository.findMessagesBetween(u1, u2);
    }

    public List<Notification> getNotifications(String userId) {
        return repository.findNotificationsByUserId(userId);
    }

    public void markNotificationRead(String notificationId) {
        repository.markNotificationAsRead(notificationId);
    }

    public HackathonTeam createHackathonTeam(String organizerId, String title, String hackathonName,
                                            String desc, String lookingFor, int maxMembers) {
        User user = repository.findUserById(organizerId);
        if (user == null) throw new IllegalArgumentException("Organizer not found.");

        HackathonTeam team = new HackathonTeam(
            SecurityUtils.generateId(),
            organizerId,
            user.getName(),
            title,
            hackathonName,
            desc,
            lookingFor,
            maxMembers
        );
        repository.saveHackathonTeam(team);
        return team;
    }

    public boolean joinHackathonTeam(String userId, String teamId, String role) {
        User user = repository.findUserById(userId);
        if (user == null) throw new IllegalArgumentException("User not found.");

        HackathonTeam team = repository.findHackathonTeamById(teamId);
        if (team == null) throw new IllegalArgumentException("Team not found.");

        boolean added = team.addMember(userId, user.getName(), role != null ? role : "Collaborator");
        if (added) {
            repository.saveHackathonTeam(team);
            repository.saveNotification(new Notification(
                SecurityUtils.generateId(),
                team.getOrganizerId(),
                "Teammate Joined Your Hackathon Team!",
                user.getName() + " joined " + team.getTitle() + " as " + role + ".",
                "#community"
            ));
        }
        return added;
    }

    public List<HackathonTeam> getAllHackathonTeams() {
        return repository.findAllHackathonTeams();
    }

    public List<Map<String, Object>> getLeaderboard() {
        List<User> users = repository.findAllUsers();
        List<Map<String, Object>> ranked = new ArrayList<>();

        for (User u : users) {
            if (u.isBanned()) continue;
            int verifiedSkills = 0;
            for (Skill s : u.getSkills()) {
                if (s.isVerified()) verifiedSkills++;
            }

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", u.getId());
            row.put("name", u.getName());
            row.put("avatarUrl", u.getAvatarUrl());
            row.put("headline", u.getHeadline());
            row.put("college", u.getCollege());
            row.put("tier", u.getTier());
            row.put("credits", u.getCredits());
            row.put("streakDays", u.getStreakDays());
            row.put("verifiedSkillsCount", verifiedSkills);
            ranked.add(row);
        }

        // Sort by Credits descending, then Streak
        ranked.sort((a, b) -> {
            int crA = (Integer) a.get("credits");
            int crB = (Integer) b.get("credits");
            if (crA != crB) return Integer.compare(crB, crA);
            return Integer.compare((Integer) b.get("streakDays"), (Integer) a.get("streakDays"));
        });

        // Add rank positions
        for (int i = 0; i < ranked.size(); i++) {
            ranked.get(i).put("rank", i + 1);
        }

        return ranked;
    }

    public KnowledgePost createKnowledgePost(String authorId, String title, String category, String tags, String content) {
        User user = repository.findUserById(authorId);
        if (user == null) throw new IllegalArgumentException("Author not found.");

        KnowledgePost post = new KnowledgePost(
            SecurityUtils.generateId(),
            authorId,
            user.getName(),
            title,
            category,
            tags,
            content
        );
        repository.saveKnowledgePost(post);
        return post;
    }

    public KnowledgePost toggleUpvoteKnowledge(String userId, String postId) {
        KnowledgePost post = repository.findKnowledgePostById(postId);
        if (post == null) throw new IllegalArgumentException("Post not found.");
        post.toggleUpvote(userId);
        repository.saveKnowledgePost(post);
        return post;
    }

    public void addKnowledgeAnswer(String responderId, String postId, String answer) {
        KnowledgePost post = repository.findKnowledgePostById(postId);
        if (post == null) throw new IllegalArgumentException("Post not found.");
        User responder = repository.findUserById(responderId);
        String name = responder != null ? responder.getName() : "Campus Peer";
        post.addAnswer(responderId, name, answer);
        repository.saveKnowledgePost(post);
    }

    public List<KnowledgePost> getKnowledgePosts() {
        return repository.findAllKnowledgePosts();
    }

    public List<Roadmap> getRoadmaps() {
        return repository.findAllRoadmaps();
    }

    public Roadmap getRoadmap(String id) {
        return repository.findRoadmapById(id);
    }
}

