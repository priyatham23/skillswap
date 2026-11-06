package com.skillswap.repository;

import com.skillswap.model.*;
import com.skillswap.util.SecurityUtils;
import com.skillswap.util.SimpleJson;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HybridDataRepository implements DataRepository {
    private static HybridDataRepository instance;

    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final List<CreditTransaction> creditLedger = Collections.synchronizedList(new ArrayList<>());
    private final List<Review> reviews = Collections.synchronizedList(new ArrayList<>());
    private final List<Message> messages = Collections.synchronizedList(new ArrayList<>());
    private final List<Notification> notifications = Collections.synchronizedList(new ArrayList<>());
    private final List<HackathonTeam> hackathonTeams = Collections.synchronizedList(new ArrayList<>());
    private final List<QuizQuestion> quizQuestions = Collections.synchronizedList(new ArrayList<>());
    private final List<Roadmap> roadmaps = Collections.synchronizedList(new ArrayList<>());
    private final List<KnowledgePost> knowledgePosts = Collections.synchronizedList(new ArrayList<>());
    private final PayItForwardPool payItForwardPool = new PayItForwardPool();

    private final String snapshotPath = "data/skillswap_datastore.json";

    private HybridDataRepository() {
        loadFromSnapshot();
        if (users.isEmpty()) {
            seedInitialData();
            persistSnapshot();
        }
        syncToDatabase();
    }

    public static synchronized HybridDataRepository getInstance() {
        if (instance == null) {
            instance = new HybridDataRepository();
        }
        return instance;
    }

    @Override
    public void saveUser(User user) {
        users.put(user.getId(), user);
        persistSnapshot();
        persistUserToDb(user);
    }

    @Override
    public User findUserById(String id) {
        return users.get(id);
    }

    @Override
    public User findUserByEmail(String email) {
        if (email == null) return null;
        for (User u : users.values()) {
            if (email.equalsIgnoreCase(u.getEmail())) {
                return u;
            }
        }
        return null;
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(String id) {
        users.remove(id);
        persistSnapshot();
    }

    @Override
    public void saveSession(Session session) {
        sessions.put(session.getId(), session);
        persistSnapshot();
        persistSessionToDb(session);
    }

    @Override
    public Session findSessionById(String id) {
        return sessions.get(id);
    }

    @Override
    public List<Session> findSessionsByUserId(String userId) {
        List<Session> res = new ArrayList<>();
        for (Session s : sessions.values()) {
            if (userId.equals(s.getTeacherId()) || userId.equals(s.getLearnerId())) {
                res.add(s);
            } else if (s instanceof GroupClassSession) {
                GroupClassSession g = (GroupClassSession) s;
                if (g.getEnrolledLearnerIds().contains(userId)) {
                    res.add(s);
                }
            }
        }
        res.sort((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()));
        return res;
    }

    @Override
    public List<Session> findAllSessions() {
        List<Session> res = new ArrayList<>(sessions.values());
        res.sort((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()));
        return res;
    }

    @Override
    public void logCreditTransaction(CreditTransaction transaction) {
        creditLedger.add(0, transaction);
        persistSnapshot();
        persistTransactionToDb(transaction);
    }

    @Override
    public List<CreditTransaction> findTransactionsByUserId(String userId) {
        List<CreditTransaction> list = new ArrayList<>();
        for (CreditTransaction t : creditLedger) {
            if (userId.equals(t.getUserId())) {
                list.add(t);
            }
        }
        return list;
    }

    @Override
    public void saveReview(Review review) {
        reviews.add(0, review);
        persistSnapshot();
        persistReviewToDb(review);
    }

    @Override
    public List<Review> findReviewsByTeacherId(String teacherId) {
        List<Review> res = new ArrayList<>();
        for (Review r : reviews) {
            if (teacherId.equals(r.getTeacherId())) {
                res.add(r);
            }
        }
        return res;
    }

    @Override
    public void saveMessage(Message message) {
        messages.add(message);
        persistSnapshot();
    }

    @Override
    public List<Message> findMessagesBetween(String user1, String user2) {
        List<Message> res = new ArrayList<>();
        for (Message m : messages) {
            if ((m.getSenderId().equals(user1) && m.getReceiverId().equals(user2)) ||
                (m.getSenderId().equals(user2) && m.getReceiverId().equals(user1))) {
                res.add(m);
            }
        }
        res.sort(Comparator.comparingLong(Message::getCreatedAt));
        return res;
    }

    @Override
    public void saveNotification(Notification notification) {
        notifications.add(0, notification);
        persistSnapshot();
    }

    @Override
    public List<Notification> findNotificationsByUserId(String userId) {
        List<Notification> res = new ArrayList<>();
        for (Notification n : notifications) {
            if (userId.equals(n.getUserId())) {
                res.add(n);
            }
        }
        return res;
    }

    @Override
    public void markNotificationAsRead(String notificationId) {
        for (Notification n : notifications) {
            if (n.getId().equals(notificationId)) {
                n.setRead(true);
                break;
            }
        }
        persistSnapshot();
    }

    @Override
    public void saveHackathonTeam(HackathonTeam team) {
        for (int i = 0; i < hackathonTeams.size(); i++) {
            if (hackathonTeams.get(i).getId().equals(team.getId())) {
                hackathonTeams.set(i, team);
                persistSnapshot();
                return;
            }
        }
        hackathonTeams.add(0, team);
        persistSnapshot();
    }

    @Override
    public List<HackathonTeam> findAllHackathonTeams() {
        return new ArrayList<>(hackathonTeams);
    }

    @Override
    public HackathonTeam findHackathonTeamById(String id) {
        for (HackathonTeam t : hackathonTeams) {
            if (t.getId().equals(id)) return t;
        }
        return null;
    }

    @Override
    public List<QuizQuestion> findQuizQuestionsBySkill(String skillName) {
        List<QuizQuestion> res = new ArrayList<>();
        for (QuizQuestion q : quizQuestions) {
            if (q.getSkillName().equalsIgnoreCase(skillName)) {
                res.add(q);
            }
        }
        return res;
    }

    @Override
    public void saveQuizQuestion(QuizQuestion question) {
        quizQuestions.add(question);
        persistSnapshot();
    }

    @Override
    public List<Roadmap> findAllRoadmaps() {
        return new ArrayList<>(roadmaps);
    }

    @Override
    public Roadmap findRoadmapById(String id) {
        for (Roadmap r : roadmaps) {
            if (r.getId().equals(id)) return r;
        }
        return null;
    }

    @Override
    public void saveKnowledgePost(KnowledgePost post) {
        for (int i = 0; i < knowledgePosts.size(); i++) {
            if (knowledgePosts.get(i).getId().equals(post.getId())) {
                knowledgePosts.set(i, post);
                persistSnapshot();
                return;
            }
        }
        knowledgePosts.add(0, post);
        persistSnapshot();
    }

    @Override
    public List<KnowledgePost> findAllKnowledgePosts() {
        return new ArrayList<>(knowledgePosts);
    }

    @Override
    public KnowledgePost findKnowledgePostById(String id) {
        for (KnowledgePost p : knowledgePosts) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    @Override
    public PayItForwardPool getPayItForwardPool() {
        return payItForwardPool;
    }

    @Override
    public boolean isMySqlConnected() {
        return DatabaseManager.getInstance().isConnected();
    }

    @Override
    public String getDatabaseStatus() {
        return DatabaseManager.getInstance().getStatusMessage();
    }

    // ==================== DB SYNC & WRITE-THROUGH ====================

    private void persistUserToDb(User u) {
        if (!isMySqlConnected()) return;
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "INSERT INTO users (id, name, email, password_hash, salt, avatar_url, headline, bio, college, linkedin_url, github_username, tier, tier_price, credits, escrow_credits, streak_days, role, is_banned) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE name=VALUES(name), avatar_url=VALUES(avatar_url), headline=VALUES(headline), bio=VALUES(bio), college=VALUES(college), " +
                         "linkedin_url=VALUES(linkedin_url), github_username=VALUES(github_username), tier=VALUES(tier), tier_price=VALUES(tier_price), " +
                         "credits=VALUES(credits), escrow_credits=VALUES(escrow_credits), streak_days=VALUES(streak_days), role=VALUES(role), is_banned=VALUES(is_banned)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, u.getId());
                ps.setString(2, u.getName());
                ps.setString(3, u.getEmail());
                ps.setString(4, u.getPasswordHash());
                ps.setString(5, u.getSalt());
                ps.setString(6, u.getAvatarUrl());
                ps.setString(7, u.getHeadline());
                ps.setString(8, u.getBio());
                ps.setString(9, u.getCollege());
                ps.setString(10, u.getLinkedinUrl());
                ps.setString(11, u.getGithubUsername());
                ps.setString(12, u.getTier());
                ps.setInt(13, u.getTierPrice());
                ps.setInt(14, u.getCredits());
                ps.setInt(15, u.getEscrowCredits());
                ps.setInt(16, u.getStreakDays());
                ps.setString(17, u.getRole());
                ps.setBoolean(18, u.isBanned());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("[HybridDataRepository] Error writing user to MySQL: " + e.getMessage());
        }
    }

    private void persistSessionToDb(Session s) {
        if (!isMySqlConnected()) return;
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "INSERT INTO sessions (id, session_type, teacher_id, learner_id, skill_topic, description, scheduled_time, duration_minutes, credit_amount, status, meeting_link) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE status=VALUES(status), scheduled_time=VALUES(scheduled_time), meeting_link=VALUES(meeting_link)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, s.getId());
                ps.setString(2, s.getSessionType());
                ps.setString(3, s.getTeacherId());
                ps.setString(4, s.getLearnerId());
                ps.setString(5, s.getSkillTopic());
                ps.setString(6, s.getDescription());
                ps.setString(7, s.getScheduledTime());
                ps.setInt(8, s.getDurationMinutes());
                ps.setInt(9, s.getCreditAmount());
                ps.setString(10, s.getStatus().name());
                ps.setString(11, s.getMeetingLink());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("[HybridDataRepository] Error writing session to MySQL: " + e.getMessage());
        }
    }

    private void persistTransactionToDb(CreditTransaction t) {
        if (!isMySqlConnected()) return;
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "INSERT INTO credit_ledger (id, user_id, transaction_type, amount, balance_after, description, reference_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, t.getId());
                ps.setString(2, t.getUserId());
                ps.setString(3, t.getTransactionType());
                ps.setInt(4, t.getAmount());
                ps.setInt(5, t.getBalanceAfter());
                ps.setString(6, t.getDescription());
                ps.setString(7, t.getReferenceId());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("[HybridDataRepository] Error writing transaction to MySQL: " + e.getMessage());
        }
    }

    private void persistReviewToDb(Review r) {
        if (!isMySqlConnected()) return;
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "INSERT INTO reviews (id, session_id, teacher_id, learner_id, rating, review_text) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, r.getId());
                ps.setString(2, r.getSessionId());
                ps.setString(3, r.getTeacherId());
                ps.setString(4, r.getLearnerId());
                ps.setInt(5, r.getRating());
                ps.setString(6, r.getReviewText());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("[HybridDataRepository] Error writing review to MySQL: " + e.getMessage());
        }
    }

    @Override
    public void syncToDatabase() {
        if (!isMySqlConnected()) return;
        for (User u : users.values()) {
            persistUserToDb(u);
        }
        for (Session s : sessions.values()) {
            persistSessionToDb(s);
        }
    }

    // ==================== PERSISTENCE (SNAPSHOT) ====================

    private synchronized void persistSnapshot() {
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            List<Map<String, Object>> uList = new ArrayList<>();
            for (User u : users.values()) {
                Map<String, Object> um = u.toMap(true);
                um.put("passwordHash", u.getPasswordHash());
                um.put("salt", u.getSalt());
                uList.add(um);
            }
            data.put("users", uList);

            List<Map<String, Object>> sList = new ArrayList<>();
            for (Session s : sessions.values()) sList.add(s.toMap());
            data.put("sessions", sList);

            List<Map<String, Object>> tList = new ArrayList<>();
            for (CreditTransaction t : creditLedger) tList.add(t.toMap());
            data.put("ledger", tList);

            List<Map<String, Object>> rList = new ArrayList<>();
            for (Review r : reviews) rList.add(r.toMap());
            data.put("reviews", rList);

            List<Map<String, Object>> mList = new ArrayList<>();
            for (Message m : messages) mList.add(m.toMap());
            data.put("messages", mList);

            List<Map<String, Object>> nList = new ArrayList<>();
            for (Notification n : notifications) nList.add(n.toMap());
            data.put("notifications", nList);

            List<Map<String, Object>> hList = new ArrayList<>();
            for (HackathonTeam h : hackathonTeams) hList.add(h.toMap());
            data.put("hackathons", hList);

            List<Map<String, Object>> qList = new ArrayList<>();
            for (QuizQuestion q : quizQuestions) qList.add(q.toMap(true));
            data.put("quizzes", qList);

            List<Map<String, Object>> rmList = new ArrayList<>();
            for (Roadmap rm : roadmaps) rmList.add(rm.toMap());
            data.put("roadmaps", rmList);

            List<Map<String, Object>> kList = new ArrayList<>();
            for (KnowledgePost k : knowledgePosts) kList.add(k.toMap());
            data.put("knowledge", kList);

            data.put("pool", payItForwardPool.toMap());

            String json = SimpleJson.toJson(data);
            Files.write(Paths.get(snapshotPath), json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.err.println("[HybridDataRepository] Snapshot save error: " + e.getMessage());
        }
    }

    private synchronized void loadFromSnapshot() {
        File file = new File(snapshotPath);
        if (!file.exists()) return;
        try {
            String json = new String(Files.readAllBytes(Paths.get(snapshotPath)), StandardCharsets.UTF_8);
            Map<String, Object> data = SimpleJson.parseObject(json);
            if (data == null) return;

            // Load Users
            List<Object> uList = (List<Object>) data.get("users");
            if (uList != null) {
                for (Object item : uList) {
                    Map<String, Object> um = (Map<String, Object>) item;
                    User u = new User();
                    u.setId((String) um.get("id"));
                    u.setName((String) um.get("name"));
                    u.setEmail((String) um.get("email"));
                    u.setPasswordHash((String) um.get("passwordHash"));
                    u.setSalt((String) um.get("salt"));
                    u.setAvatarUrl((String) um.get("avatarUrl"));
                    u.setHeadline((String) um.get("headline"));
                    u.setBio((String) um.get("bio"));
                    u.setCollege((String) um.get("college"));
                    u.setLinkedinUrl((String) um.get("linkedinUrl"));
                    u.setGithubUsername((String) um.get("githubUsername"));
                    u.setTier((String) um.get("tier"));
                    u.setTierPrice(um.get("tierPrice") != null ? ((Number) um.get("tierPrice")).intValue() : 10);
                    u.setCredits(um.get("credits") != null ? ((Number) um.get("credits")).intValue() : 100);
                    u.setEscrowCredits(um.get("escrowCredits") != null ? ((Number) um.get("escrowCredits")).intValue() : 0);
                    u.setStreakDays(um.get("streakDays") != null ? ((Number) um.get("streakDays")).intValue() : 1);
                    u.setRole(um.get("role") != null ? (String) um.get("role") : "USER");
                    u.setBanned(Boolean.TRUE.equals(um.get("isBanned")));
                    if (um.get("availableFormats") != null) u.setAvailableFormats((String) um.get("availableFormats"));
                    if (um.get("availableTimeSlots") != null) u.setAvailableTimeSlots((String) um.get("availableTimeSlots"));
                    if (um.get("mentorIntro") != null) u.setMentorIntro((String) um.get("mentorIntro"));
                    if (um.get("expertiseLevel") != null) u.setExpertiseLevel((String) um.get("expertiseLevel"));
                    if (um.get("createdAt") != null) u.setCreatedAt(((Number) um.get("createdAt")).longValue());

                    // Skills
                    List<Object> sList = (List<Object>) um.get("skills");
                    if (sList != null) {
                        for (Object so : sList) {
                            Map<String, Object> sm = (Map<String, Object>) so;
                            Skill s = new Skill(
                                (String) sm.get("id"),
                                (String) sm.get("skillName"),
                                (String) sm.get("skillType"),
                                (String) sm.get("proficiencyLevel"),
                                Boolean.TRUE.equals(sm.get("isVerified")),
                                sm.get("quizScore") != null ? ((Number) sm.get("quizScore")).intValue() : 0
                            );
                            u.getSkills().add(s);
                        }
                    }

                    // Education
                    List<Object> eList = (List<Object>) um.get("educations");
                    if (eList != null) {
                        for (Object eo : eList) {
                            Map<String, Object> em = (Map<String, Object>) eo;
                            Education e = new Education(
                                (String) em.get("id"),
                                (String) em.get("school"),
                                (String) em.get("degree"),
                                (String) em.get("fieldOfStudy"),
                                em.get("startYear") != null ? ((Number) em.get("startYear")).intValue() : 2022,
                                em.get("endYear") != null ? ((Number) em.get("endYear")).intValue() : 2026
                            );
                            u.getEducations().add(e);
                        }
                    }

                    // Projects
                    List<Object> pList = (List<Object>) um.get("projects");
                    if (pList != null) {
                        for (Object po : pList) {
                            Map<String, Object> pm = (Map<String, Object>) po;
                            Project p = new Project(
                                (String) pm.get("id"),
                                (String) pm.get("title"),
                                (String) pm.get("description"),
                                (String) pm.get("githubRepoUrl"),
                                (String) pm.get("liveUrl"),
                                (String) pm.get("tags"),
                                pm.get("stars") != null ? ((Number) pm.get("stars")).intValue() : 0,
                                Boolean.TRUE.equals(pm.get("isImported"))
                            );
                            u.getProjects().add(p);
                        }
                    }

                    // Certificates
                    List<Object> cList = (List<Object>) um.get("certificates");
                    if (cList != null) {
                        for (Object co : cList) {
                            Map<String, Object> cm = (Map<String, Object>) co;
                            Certificate c = new Certificate(
                                (String) cm.get("id"),
                                (String) cm.get("title"),
                                (String) cm.get("issuingOrg"),
                                (String) cm.get("issueDate"),
                                (String) cm.get("credentialUrl"),
                                (String) cm.get("proofDescription")
                            );
                            if (cm.get("status") != null) c.setStatus((String) cm.get("status"));
                            if (cm.get("adminNotes") != null) c.setAdminNotes((String) cm.get("adminNotes"));
                            u.getCertificates().add(c);
                        }
                    }

                    users.put(u.getId(), u);
                }
            }

            // Load Quizzes, Roadmaps, etc.
            loadAuxiliaryData(data);

        } catch (Exception e) {
            System.err.println("[HybridDataRepository] Snapshot load error: " + e.getMessage());
        }
    }

    private void loadAuxiliaryData(Map<String, Object> data) {
        // Quizzes
        List<Object> qList = (List<Object>) data.get("quizzes");
        if (qList != null) {
            for (Object qo : qList) {
                Map<String, Object> qm = (Map<String, Object>) qo;
                List<String> opts = new ArrayList<>();
                List<Object> optObjs = (List<Object>) qm.get("options");
                if (optObjs != null) {
                    for (Object o : optObjs) opts.add(String.valueOf(o));
                }
                QuizQuestion qq = new QuizQuestion(
                    (String) qm.get("id"),
                    (String) qm.get("skillName"),
                    (String) qm.get("question"),
                    opts,
                    qm.get("correctOptionIndex") != null ? ((Number) qm.get("correctOptionIndex")).intValue() : 0,
                    (String) qm.get("explanation")
                );
                quizQuestions.add(qq);
            }
        }

        // Roadmaps
        List<Object> rmList = (List<Object>) data.get("roadmaps");
        if (rmList != null) {
            for (Object rmo : rmList) {
                Map<String, Object> rmm = (Map<String, Object>) rmo;
                Roadmap r = new Roadmap(
                    (String) rmm.get("id"),
                    (String) rmm.get("title"),
                    (String) rmm.get("role"),
                    (String) rmm.get("description"),
                    (String) rmm.get("duration"),
                    (String) rmm.get("icon")
                );
                List<Object> sObjs = (List<Object>) rmm.get("steps");
                if (sObjs != null) {
                    for (Object so : sObjs) {
                        Map<String, Object> sm = (Map<String, Object>) so;
                        List<String> res = new ArrayList<>();
                        List<Object> resObjs = (List<Object>) sm.get("resources");
                        if (resObjs != null) {
                            for (Object ro : resObjs) res.add(String.valueOf(ro));
                        }
                        r.getSteps().add(new Roadmap.RoadmapStep(
                            (String) sm.get("stepId"),
                            (String) sm.get("title"),
                            (String) sm.get("description"),
                            (String) sm.get("skillToSwap"),
                            res
                        ));
                    }
                }
                roadmaps.add(r);
            }
        }

        // Knowledge
        List<Object> kList = (List<Object>) data.get("knowledge");
        if (kList != null) {
            for (Object ko : kList) {
                Map<String, Object> km = (Map<String, Object>) ko;
                KnowledgePost kp = new KnowledgePost(
                    (String) km.get("id"),
                    (String) km.get("authorId"),
                    (String) km.get("authorName"),
                    (String) km.get("title"),
                    (String) km.get("category"),
                    (String) km.get("tags"),
                    (String) km.get("content")
                );
                if (km.get("upvotes") != null) kp.setUpvotes(((Number) km.get("upvotes")).intValue());
                knowledgePosts.add(kp);
            }
        }
    }

    // ==================== SEED INITIAL DEMO DATA ====================

    private void seedInitialData() {
        System.out.println("[HybridDataRepository] Seeding rich campus demo dataset...");

        // Admin User
        String adminSalt = SecurityUtils.generateSalt();
        User admin = new User(
            "admin-1",
            "Prof. Admin (Dean of Studies)",
            "admin@skillswap.edu",
            SecurityUtils.hashPassword("admin123", adminSalt),
            adminSalt
        );
        admin.setRole("ADMIN");
        admin.setHeadline("Campus Dean & Platform Verification Admin");
        admin.setBio("Overseeing campus peer mentorship and validating verified skill credentials.");
        admin.setCollege("Stanford / IIT Campus Network");
        admin.setAvatarUrl("https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=250&q=80");
        admin.setTier("Pro");
        users.put(admin.getId(), admin);

        // User 1: Priya Sharma (React, TypeScript teacher; wants to learn Machine Learning)
        String s1 = SecurityUtils.generateSalt();
        User u1 = new User("user-1", "Priya Sharma", "priya@skillswap.edu", SecurityUtils.hashPassword("priya123", s1), s1);
        u1.setHeadline("Frontend Architect | React 19 & Tailwind Specialist");
        u1.setBio("Senior CS student with 2 internships. Love building responsive UI systems and helping juniors master modern frontend.");
        u1.setCollege("Birla Institute of Technology (BITS)");
        u1.setGithubUsername("priya-frontend");
        u1.setLinkedinUrl("https://linkedin.com/in/priya-sharma-demo");
        u1.setTier("Pro");
        u1.setTierPrice(50);
        u1.setCredits(240);
        u1.setStreakDays(14);
        u1.setAvatarUrl("https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=250&q=80");
        u1.getSkills().add(new Skill(SecurityUtils.generateId(), "React", "TEACH", "Expert", true, 95));
        u1.getSkills().add(new Skill(SecurityUtils.generateId(), "TypeScript", "TEACH", "Advanced", true, 90));
        u1.getSkills().add(new Skill(SecurityUtils.generateId(), "Machine Learning", "LEARN", "Beginner", false, 0));
        u1.getSkills().add(new Skill(SecurityUtils.generateId(), "DSA", "LEARN", "Intermediate", false, 0));
        u1.getEducations().add(new Education(SecurityUtils.generateId(), "BITS Pilani", "B.Tech", "Computer Science", 2022, 2026));
        u1.getProjects().add(new Project(SecurityUtils.generateId(), "Kanban Flow Studio", "Collaborative drag-and-drop workspace", "https://github.com/priya/kanban-flow", "https://kanban-demo.io", "React, Tailwind, WebSockets", 124, false));
        u1.getProjects().add(new Project(SecurityUtils.generateId(), "CSS Neumorphic Design Kit", "Popular UI design kit with 2k stars", "https://github.com/priya/neumorphic-ui", "", "CSS, Vanilla JS", 210, true));
        Certificate c1 = new Certificate(SecurityUtils.generateId(), "Meta Certified Frontend Developer", "Meta", "March 2025", "https://coursera.org/meta-cert-123", "Verified professional certificate in React and state management.");
        c1.setStatus("APPROVED");
        u1.getCertificates().add(c1);
        users.put(u1.getId(), u1);

        // User 2: Rahul Verma (Machine Learning teacher; wants to learn Java & DSA)
        String s2 = SecurityUtils.generateSalt();
        User u2 = new User("user-2", "Rahul Verma", "rahul@skillswap.edu", SecurityUtils.hashPassword("rahul123", s2), s2);
        u2.setHeadline("AI/ML Researcher | PyTorch, NLP & Computer Vision");
        u2.setBio("Published 1 paper on transformer attention optimizations. Passionate about machine learning foundations.");
        u2.setCollege("IIT Bombay");
        u2.setGithubUsername("rahul-ml");
        u2.setLinkedinUrl("https://linkedin.com/in/rahul-verma-ml");
        u2.setTier("Intermediate");
        u2.setTierPrice(25);
        u2.setCredits(180);
        u2.setStreakDays(7);
        u2.setAvatarUrl("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=250&q=80");
        u2.getSkills().add(new Skill(SecurityUtils.generateId(), "Machine Learning", "TEACH", "Advanced", true, 88));
        u2.getSkills().add(new Skill(SecurityUtils.generateId(), "Python", "TEACH", "Expert", true, 94));
        u2.getSkills().add(new Skill(SecurityUtils.generateId(), "Java", "LEARN", "Beginner", false, 0));
        u2.getEducations().add(new Education(SecurityUtils.generateId(), "IIT Bombay", "B.Tech", "Electrical & CS", 2023, 2027));
        u2.getProjects().add(new Project(SecurityUtils.generateId(), "Edge Vision Face Detector", "Real-time edge inference model", "https://github.com/rahul/edge-vision", "", "PyTorch, OpenCV", 85, true));
        Certificate c2 = new Certificate(SecurityUtils.generateId(), "DeepLearning.AI TensorFlow Specialization", "DeepLearning.AI", "Jan 2025", "https://deeplearning.ai/certs/tf-99", "Proof of deep learning sequence models completion.");
        c2.setStatus("APPROVED");
        u2.getCertificates().add(c2);
        users.put(u2.getId(), u2);

        // User 3: Ananya Patel (Java & DSA teacher; wants to learn React!)
        // Notice: Priya teaches React -> Rahul teaches ML -> Ananya teaches Java -> forms a 3-way cycle!
        String s3 = SecurityUtils.generateSalt();
        User u3 = new User("user-3", "Ananya Patel", "ananya@skillswap.edu", SecurityUtils.hashPassword("ananya123", s3), s3);
        u3.setHeadline("LeetCode Knight (2100+) | Java & Spring Boot Enthusiast");
        u3.setBio("Cracked multiple backend interviews. Love breaking down complex graph and dynamic programming problems.");
        u3.setCollege("Delhi Technological University (DTU)");
        u3.setGithubUsername("ananya-code");
        u3.setLinkedinUrl("https://linkedin.com/in/ananya-patel-dsa");
        u3.setTier("Intermediate");
        u3.setTierPrice(25);
        u3.setCredits(150);
        u3.setStreakDays(9);
        u3.setAvatarUrl("https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=250&q=80");
        u3.getSkills().add(new Skill(SecurityUtils.generateId(), "Java", "TEACH", "Expert", true, 92));
        u3.getSkills().add(new Skill(SecurityUtils.generateId(), "DSA", "TEACH", "Expert", true, 96));
        u3.getSkills().add(new Skill(SecurityUtils.generateId(), "React", "LEARN", "Beginner", false, 0));
        u3.getEducations().add(new Education(SecurityUtils.generateId(), "DTU", "B.Tech", "Information Technology", 2022, 2026));
        u3.getProjects().add(new Project(SecurityUtils.generateId(), "Distributed Job Scheduler", "Fault-tolerant Redis queue in Java", "https://github.com/ananya/job-scheduler", "", "Java 21, Spring Boot, Redis", 140, false));
        Certificate c3 = new Certificate(SecurityUtils.generateId(), "Oracle Certified Professional: Java SE 17", "Oracle", "August 2024", "https://oracle.com/certs/ocp-java-88", "Passed OCP exam with 93% score.");
        c3.setStatus("APPROVED");
        u3.getCertificates().add(c3);
        users.put(u3.getId(), u3);

        // User 4: Rohan Mehta (System Design teacher, wants Golang)
        String s4 = SecurityUtils.generateSalt();
        User u4 = new User("user-4", "Rohan Mehta", "rohan@skillswap.edu", SecurityUtils.hashPassword("rohan123", s4), s4);
        u4.setHeadline("Cloud Systems Specialist | AWS & High-Scale Architecture");
        u4.setBio("Ex-Amazon Intern. Mentoring students on scalable distributed systems, caching, and database partitioning.");
        u4.setCollege("IIIT Hyderabad");
        u4.setGithubUsername("rohan-cloud");
        u4.setLinkedinUrl("https://linkedin.com/in/rohan-mehta-cloud");
        u4.setTier("Pro");
        u4.setTierPrice(50);
        u4.setCredits(320);
        u4.setStreakDays(21);
        u4.setAvatarUrl("https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=250&q=80");
        u4.getSkills().add(new Skill(SecurityUtils.generateId(), "System Design", "TEACH", "Expert", true, 98));
        u4.getSkills().add(new Skill(SecurityUtils.generateId(), "Golang", "LEARN", "Intermediate", false, 0));
        users.put(u4.getId(), u4);

        // User 5: Sneha Reddy (Golang teacher, wants System Design) -> Direct Swap with Rohan!
        String s5 = SecurityUtils.generateSalt();
        User u5 = new User("user-5", "Sneha Reddy", "sneha@skillswap.edu", SecurityUtils.hashPassword("sneha123", s5), s5);
        u5.setHeadline("Backend & Microservices Engineer | Go & Kubernetes");
        u5.setBio("Passionate about high-throughput concurrency in Go. Seeking deeper knowledge in high-level system design tradeoffs.");
        u5.setCollege("NIT Trichy");
        u5.setGithubUsername("sneha-go");
        u5.setLinkedinUrl("https://linkedin.com/in/sneha-reddy-go");
        u5.setTier("Pro");
        u5.setTierPrice(50);
        u5.setCredits(290);
        u5.setStreakDays(18);
        u5.setAvatarUrl("https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=250&q=80");
        u5.getSkills().add(new Skill(SecurityUtils.generateId(), "Golang", "TEACH", "Expert", true, 94));
        u5.getSkills().add(new Skill(SecurityUtils.generateId(), "System Design", "LEARN", "Intermediate", false, 0));
        users.put(u5.getId(), u5);

        // Seed Sample Sessions
        DirectSwapSession dss = new DirectSwapSession(
            "sess-1",
            u4.getId(),
            u5.getId(),
            "High-Throughput Rate Limiter Design",
            "Pair session designing Token Bucket and Leaky Bucket in distributed Redis.",
            "Tomorrow, 6:00 PM",
            60,
            25,
            "Golang Goroutines & Channels"
        );
        dss.setStatus(SessionStatus.ACCEPTED);
        sessions.put(dss.getId(), dss);

        GroupClassSession gcs = new GroupClassSession(
            "sess-2",
            u1.getId(),
            "Modern React 19 State Architecture & Hooks Masterclass",
            "Comprehensive 90-min live session covering Server Components, Actions, and optimistic UI transitions.",
            "Saturday, 4:00 PM",
            12,
            15
        );
        gcs.enrollLearner(u3.getId());
        gcs.setStatus(SessionStatus.ACCEPTED);
        sessions.put(gcs.getId(), gcs);

        DoubtSprintSession dbs = new DoubtSprintSession(
            "sess-3",
            u2.getId(),
            u1.getId(),
            "PyTorch CUDA out of memory error during backward pass",
            "Gradient tensor accumulation bug inside custom training loop. Need 15 min quick fix.",
            "HIGH",
            "RuntimeError: CUDA out of memory. Tried to allocate 2.40 GiB (GPU 0; 8.00 GiB total capacity)"
        );
        dbs.setStatus(SessionStatus.COMPLETED);
        sessions.put(dbs.getId(), dbs);

        // Seed Reviews
        reviews.add(new Review(SecurityUtils.generateId(), dbs.getId(), u2.getId(), u1.getId(), "Priya Sharma", 5,
            "Rahul spotted the memory leak in under 5 minutes! Unblocked my submission just in time. Incredible mentor."));

        // Seed Ledger
        creditLedger.add(new CreditTransaction(SecurityUtils.generateId(), u1.getId(), "SIGNUP_BONUS", 100, 100, "Campus Sign-Up Welcome Bonus", "SYS"));
        creditLedger.add(new CreditTransaction(SecurityUtils.generateId(), u1.getId(), "TEACHING_REWARD", 50, 150, "Completed React Mentorship Session", "sess-10"));
        creditLedger.add(new CreditTransaction(SecurityUtils.generateId(), u1.getId(), "PURCHASE", 100, 250, "Recharged wallet (Demo College Pass)", "TXN-9981"));
        creditLedger.add(new CreditTransaction(SecurityUtils.generateId(), u1.getId(), "ESCROW_HOLD", -10, 240, "Escrow held for Group Class", gcs.getId()));

        // Seed Quizzes
        seedQuizzes();

        // Seed Roadmaps
        seedRoadmaps();

        // Seed Hackathon Teams
        HackathonTeam ht1 = new HackathonTeam(SecurityUtils.generateId(), u1.getId(), u1.getName(),
            "MedAI - Diagnostic Assistant for Rural Clinics", "Smart India Hackathon 2026",
            "Building an offline-first mobile web app with quantized local LLMs for symptom triage.",
            "Machine Learning Engineer, Backend Go Developer", 4);
        ht1.addMember(u2.getId(), u2.getName(), "ML / LLM Fine-tuning");
        hackathonTeams.add(ht1);

        HackathonTeam ht2 = new HackathonTeam(SecurityUtils.generateId(), u4.getId(), u4.getName(),
            "CampusRide - Zero-Gas Peer Carpooling", "EcoHacks Global",
            "Microservice-based carpool route optimization for university commuter students.",
            "Frontend UI/UX, Flutter/React dev", 4);
        hackathonTeams.add(ht2);

        // Seed Knowledge Posts
        KnowledgePost kp1 = new KnowledgePost(SecurityUtils.generateId(), u3.getId(), u3.getName(),
            "How does Java Virtual Thread (Project Loom) handle IO-bound blocking?",
            "Java & Backend", "Java21, Concurrency, VirtualThreads",
            "Virtual threads are user-mode threads managed by the JDK rather than OS kernel threads. When a virtual thread performs a blocking I/O operation (like socket read or JDBC call with compatible driver), the runtime unmounts it from the carrier thread, parking the continuation and freeing the carrier thread for other work!");
        kp1.setUpvotes(18);
        kp1.addAnswer(u4.getId(), u4.getName(), "Exactly! It eliminates the callback hell of reactive frameworks while keeping throughput 10x higher on IO loads.");
        knowledgePosts.add(kp1);

        KnowledgePost kp2 = new KnowledgePost(SecurityUtils.generateId(), u1.getId(), u1.getName(),
            "Why use `useActionState` over manual `useState` for forms in modern React?",
            "Frontend & React", "React19, Hooks, Forms",
            "useActionState natively handles pending state, server error responses, and form reset lifecycles without requiring duplicate isSubmitting or error state variables.");
        kp2.setUpvotes(14);
        knowledgePosts.add(kp2);

        // Pay It Forward pool donations
        payItForwardPool.donate(u4.getId(), u4.getName(), 50, "Keep the campus learning spirit thriving!");
        payItForwardPool.donate(u1.getId(), u1.getName(), 30, "Happy to support freshers joining peer swaps.");
    }

    private void seedQuizzes() {
        // Java Quiz
        quizQuestions.add(new QuizQuestion(SecurityUtils.generateId(), "Java",
            "What is the time complexity of retrieving an element by key in a standard java.util.HashMap with minimal collisions?",
            Arrays.asList("O(1)", "O(log N)", "O(N)", "O(N log N)"), 0,
            "HashMap provides amortized constant time O(1) performance for get and put operations assuming well-distributed hash codes."));
        quizQuestions.add(new QuizQuestion(SecurityUtils.generateId(), "Java",
            "Which OOP principle is primarily demonstrated by method overriding?",
            Arrays.asList("Polymorphism", "Encapsulation", "Abstraction", "Composition"), 0,
            "Runtime polymorphism (dynamic method dispatch) allows a subclass method to override its superclass implementation."));
        quizQuestions.add(new QuizQuestion(SecurityUtils.generateId(), "Java",
            "Which keyword is used in Java to prevent a method from being overridden?",
            Arrays.asList("static", "final", "sealed", "const"), 1,
            "Marking a method as final prevents child classes from overriding it."));
        quizQuestions.add(new QuizQuestion(SecurityUtils.generateId(), "Java",
            "What happens if an unhandled checked exception is thrown inside a Java method?",
            Arrays.asList("It fails to compile", "It terminates quietly", "It converts to a Warning", "It retries automatically"), 0,
            "Checked exceptions must either be caught in a try-catch block or declared in the method throws signature."));

        // Python Quiz
        quizQuestions.add(new QuizQuestion(SecurityUtils.generateId(), "Python",
            "What is the GIL (Global Interpreter Lock) in CPython?",
            Arrays.asList("A lock allowing only one native thread to execute Python bytecode at a time", "A compiler optimization tool", "A database connector", "A security sandbox"), 0,
            "The GIL prevents race conditions in CPython's memory management by serializing thread bytecode execution."));
        quizQuestions.add(new QuizQuestion(SecurityUtils.generateId(), "Python",
            "Which built-in data structure preserves insertion order in Python 3.7+?",
            Arrays.asList("dict", "set", "frozenset", "heapq"), 0,
            "Standard Python dictionaries maintain key insertion order as a language specification since Python 3.7."));

        // React Quiz
        quizQuestions.add(new QuizQuestion(SecurityUtils.generateId(), "React",
            "What is the main benefit of using the `useCallback` hook in React?",
            Arrays.asList("Memoizing a callback function between renders to prevent unnecessary re-renders of optimized children", "Fetching data asynchronously", "Updating DOM directly", "Handling CSS transitions"), 0,
            "useCallback returns a memoized callback instance that only recalculates when dependencies change."));
        quizQuestions.add(new QuizQuestion(SecurityUtils.generateId(), "React",
            "Why must keys in React lists be unique and stable?",
            Arrays.asList("To help React identify which items have changed, added, or removed during Reconciliation", "To sort the array automatically", "To encrypt user data", "To style items sequentially"), 0,
            "Reconciliation uses keys to match existing DOM tree nodes to new virtual elements efficiently."));

        // DSA Quiz
        quizQuestions.add(new QuizQuestion(SecurityUtils.generateId(), "DSA",
            "Which data structure is most optimal for implementing Dijkstra's shortest path algorithm on sparse graphs?",
            Arrays.asList("Min-Heap / Priority Queue", "Stack", "Queue (FIFO)", "Sorted Doubly Linked List"), 0,
            "A Min-Heap allows extracting the minimum distance node in O(log V) time, resulting in O((V + E) log V) total complexity."));
        quizQuestions.add(new QuizQuestion(SecurityUtils.generateId(), "DSA",
            "What is the worst-case time complexity of QuickSort?",
            Arrays.asList("O(N^2)", "O(N log N)", "O(N)", "O(log N)"), 0,
            "Worst case occurs when the selected pivot is repeatedly the smallest or largest element, degrading to O(N^2)."));
    }

    private void seedRoadmaps() {
        Roadmap r1 = new Roadmap("rm-1", "Full-Stack Web Dev in 3 Months", "Full-Stack Engineer",
            "From responsive frontend mastery to production microservices and cloud deployment.", "12 Weeks", "code");
        r1.getSteps().add(new Roadmap.RoadmapStep("s1", "Modern JavaScript & DOM Internals", "Closures, Event Loop, Promises, Fetch API, ES6+ modules.", "JavaScript", Arrays.asList("MDN Web Docs", "JavaScript.info")));
        r1.getSteps().add(new Roadmap.RoadmapStep("s2", "React 19 & Component Architecture", "State machines, custom hooks, Server Actions, Tailwind CSS.", "React", Arrays.asList("React.dev Official Documentation", "Scrimba React Course")));
        r1.getSteps().add(new Roadmap.RoadmapStep("s3", "Java & Spring Boot RESTful APIs", "OOP design, JPA/Hibernate, JWT security, Transactional integrity.", "Java", Arrays.asList("Spring.io Guides", "Baeldung Tutorials")));
        r1.getSteps().add(new Roadmap.RoadmapStep("s4", "Database Design & SQL Optimization", "Relational normalization, composite indexing, query profiling, ACID.", "SQL", Arrays.asList("Use The Index Luke", "MySQL High Performance")));
        r1.getSteps().add(new Roadmap.RoadmapStep("s5", "Docker & CI/CD Deployment", "Containerization, GitHub Actions pipelines, cloud hosting.", "DevOps", Arrays.asList("Docker Docs", "GitHub Actions Mastery")));
        roadmaps.add(r1);

        Roadmap r2 = new Roadmap("rm-2", "Machine Learning & AI Foundations", "ML Engineer",
            "Mathematical foundations, classical models, deep learning transformers and deployment.", "16 Weeks", "brain");
        r2.getSteps().add(new Roadmap.RoadmapStep("m1", "Python for Scientific Computing", "NumPy vectorization, Pandas data frames, Matplotlib visualization.", "Python", Arrays.asList("Python Data Science Handbook")));
        r2.getSteps().add(new Roadmap.RoadmapStep("m2", "Supervised & Unsupervised Learning", "Linear/Logistic regression, Decision trees, SVMs, PCA, K-Means.", "Machine Learning", Arrays.asList("Scikit-Learn Documentation", "StatQuest with Josh Starmer")));
        r2.getSteps().add(new Roadmap.RoadmapStep("m3", "Neural Networks & PyTorch", "Backpropagation, CNNs, RNNs, Attention mechanisms.", "PyTorch", Arrays.asList("PyTorch.org Tutorials", "Andrej Karpathy Neural Networks from Scratch")));
        roadmaps.add(r2);
    }
}

