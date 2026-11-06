package com.skillswap.controller;

import com.skillswap.model.*;
import com.skillswap.repository.DatabaseManager;
import com.skillswap.repository.HybridDataRepository;
import com.skillswap.service.*;
import com.skillswap.util.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.*;

public class ApiController implements HttpHandler {
    private final AuthService authService;
    private final UserService userService;
    private final SkillService skillService;
    private final MatchingService matchingService;
    private final CreditService creditService;
    private final SessionService sessionService;
    private final CommunityService communityService;
    private final AdminService adminService;

    public ApiController(AuthService authService, UserService userService, SkillService skillService,
                         MatchingService matchingService, CreditService creditService,
                         SessionService sessionService, CommunityService communityService,
                         AdminService adminService) {
        this.authService = authService;
        this.userService = userService;
        this.skillService = skillService;
        this.matchingService = matchingService;
        this.creditService = creditService;
        this.sessionService = sessionService;
        this.communityService = communityService;
        this.adminService = adminService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (HttpUtils.handlePreflight(exchange)) return;

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod().toUpperCase();
        Map<String, String> query = HttpUtils.parseQueryParams(exchange);

        try {
            // Authentication Endpoints
            if (path.equals("/api/auth/register") && method.equals("POST")) {
                handleRegister(exchange);
            } else if (path.equals("/api/auth/login") && method.equals("POST")) {
                handleLogin(exchange);
            } else if (path.equals("/api/auth/logout") && method.equals("POST")) {
                handleLogout(exchange);
            } else if (path.equals("/api/auth/me") && method.equals("GET")) {
                handleGetMe(exchange);

            // Profile Endpoints
            } else if (path.equals("/api/profile") && method.equals("GET")) {
                handleGetProfile(exchange, query);
            } else if (path.equals("/api/profile") && method.equals("PUT")) {
                handleUpdateProfile(exchange);
            } else if (path.equals("/api/profile/avatar") && method.equals("POST")) {
                handleUploadAvatar(exchange);
            } else if (path.equals("/api/profile/education") && method.equals("POST")) {
                handleAddEducation(exchange);
            } else if (path.equals("/api/profile/project") && method.equals("POST")) {
                handleAddProject(exchange);
            } else if (path.equals("/api/profile/import-github") && method.equals("POST")) {
                handleImportGithub(exchange);
            } else if (path.equals("/api/profile/certificate") && method.equals("POST")) {
                handleSubmitCertificate(exchange);
            } else if (path.equals("/api/profile/passport") && method.equals("GET")) {
                handleGetPassport(exchange, query);

            // Skills & Quiz
            } else if (path.equals("/api/skills/add") && method.equals("POST")) {
                handleAddSkill(exchange);
            } else if (path.equals("/api/skills/become-mentor") && method.equals("POST")) {
                handleBecomeMentor(exchange);
            } else if (path.equals("/api/skills/delete") && method.equals("DELETE")) {
                handleDeleteSkill(exchange, query);
            } else if (path.equals("/api/skills/search") && method.equals("GET")) {
                handleSearchTeachers(exchange, query);
            } else if (path.equals("/api/quiz/questions") && method.equals("GET")) {
                handleGetQuiz(exchange, query);
            } else if (path.equals("/api/quiz/submit") && method.equals("POST")) {
                handleSubmitQuiz(exchange);

            // Matching
            } else if (path.equals("/api/matching/all") && method.equals("GET")) {
                handleGetAllMatches(exchange);

            // Credits & Escrow
            } else if (path.equals("/api/credits/buy") && method.equals("POST")) {
                handleBuyCredits(exchange);
            } else if (path.equals("/api/credits/ledger") && method.equals("GET")) {
                handleGetLedger(exchange);
            } else if (path.equals("/api/credits/pool") && method.equals("GET")) {
                handleGetPool(exchange);
            } else if (path.equals("/api/credits/pool/donate") && method.equals("POST")) {
                handleDonatePool(exchange);
            } else if (path.equals("/api/credits/pool/claim") && method.equals("POST")) {
                handleClaimPool(exchange);

            // Sessions
            } else if (path.equals("/api/sessions/request") && method.equals("POST")) {
                handleRequestSession(exchange);
            } else if (path.equals("/api/sessions/accept") && method.equals("POST")) {
                handleAcceptSession(exchange);
            } else if (path.equals("/api/sessions/reject") && method.equals("POST")) {
                handleRejectSession(exchange);
            } else if (path.equals("/api/sessions/cancel") && method.equals("POST")) {
                handleCancelSession(exchange);
            } else if (path.equals("/api/sessions/complete") && method.equals("POST")) {
                handleCompleteSession(exchange);
            } else if (path.equals("/api/sessions/group/enroll") && method.equals("POST")) {
                handleEnrollGroupClass(exchange);
            } else if (path.equals("/api/sessions/review") && method.equals("POST")) {
                handleSubmitReview(exchange);
            } else if (path.equals("/api/sessions/my") && method.equals("GET")) {
                handleGetMySessions(exchange);

            // Community
            } else if (path.equals("/api/community/message/send") && method.equals("POST")) {
                handleSendMessage(exchange);
            } else if (path.equals("/api/community/message/conversation") && method.equals("GET")) {
                handleGetConversation(exchange, query);
            } else if (path.equals("/api/community/notifications") && method.equals("GET")) {
                handleGetNotifications(exchange);
            } else if (path.equals("/api/community/notifications/read") && method.equals("POST")) {
                handleReadNotification(exchange);
            } else if (path.equals("/api/community/hackathons") && method.equals("GET")) {
                handleGetHackathons(exchange);
            } else if (path.equals("/api/community/hackathons/create") && method.equals("POST")) {
                handleCreateHackathon(exchange);
            } else if (path.equals("/api/community/hackathons/join") && method.equals("POST")) {
                handleJoinHackathon(exchange);
            } else if (path.equals("/api/community/leaderboard") && method.equals("GET")) {
                handleGetLeaderboard(exchange);
            } else if (path.equals("/api/community/knowledge") && method.equals("GET")) {
                handleGetKnowledge(exchange);
            } else if (path.equals("/api/community/knowledge/create") && method.equals("POST")) {
                handleCreateKnowledge(exchange);
            } else if (path.equals("/api/community/knowledge/upvote") && method.equals("POST")) {
                handleUpvoteKnowledge(exchange);
            } else if (path.equals("/api/community/knowledge/answer") && method.equals("POST")) {
                handleAnswerKnowledge(exchange);
            } else if (path.equals("/api/community/roadmaps") && method.equals("GET")) {
                handleGetRoadmaps(exchange);

            // Admin
            } else if (path.equals("/api/admin/users") && method.equals("GET")) {
                handleAdminGetUsers(exchange);
            } else if (path.equals("/api/admin/users/ban") && method.equals("POST")) {
                handleAdminBanUser(exchange);
            } else if (path.equals("/api/admin/certificates") && method.equals("GET")) {
                handleAdminGetCertificates(exchange);
            } else if (path.equals("/api/admin/certificates/review") && method.equals("POST")) {
                handleAdminReviewCertificate(exchange);
            } else if (path.equals("/api/admin/stats") && method.equals("GET")) {
                handleAdminStats(exchange);
            } else if (path.equals("/api/admin/db/reconnect") && method.equals("POST")) {
                handleAdminDbReconnect(exchange);

            } else {
                HttpUtils.sendError(exchange, 404, "Endpoint not found: " + path);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            HttpUtils.sendError(exchange, 400, e.getMessage());
        } catch (SecurityException e) {
            HttpUtils.sendError(exchange, 403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            HttpUtils.sendError(exchange, 500, "Server Error: " + e.getMessage());
        }
    }

    private User requireAuth(HttpExchange exchange) {
        String token = HttpUtils.getAuthToken(exchange);
        User user = authService.authenticate(token);
        if (user == null) {
            throw new SecurityException("Unauthorized. Please log in.");
        }
        return user;
    }

    // ==================== AUTH HANDLERS ====================

    private void handleRegister(HttpExchange exchange) throws IOException {
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String name = (String) body.get("name");
        String email = (String) body.get("email");
        String password = (String) body.get("password");
        String college = (String) body.get("college");

        User user = authService.register(name, email, password, college);
        String token = authService.createSessionToken(user);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("user", user.toMap(true));
        resp.put("token", token);
        HttpUtils.sendSuccess(exchange, resp);
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String email = (String) body.get("email");
        String password = (String) body.get("password");

        User user = authService.login(email, password);
        String token = authService.createSessionToken(user);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("user", user.toMap(true));
        resp.put("token", token);
        HttpUtils.sendSuccess(exchange, resp);
    }

    private void handleLogout(HttpExchange exchange) throws IOException {
        String token = HttpUtils.getAuthToken(exchange);
        authService.logout(token);
        HttpUtils.sendSuccess(exchange, "Logged out successfully.");
    }

    private void handleGetMe(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        HttpUtils.sendSuccess(exchange, user.toMap(true));
    }

    // ==================== PROFILE HANDLERS ====================

    private void handleGetProfile(HttpExchange exchange, Map<String, String> query) throws IOException {
        String uid = query.get("userId");
        if (uid == null) {
            User u = requireAuth(exchange);
            uid = u.getId();
        }
        User target = userService.getProfile(uid);
        if (target == null) throw new IllegalArgumentException("User not found.");
        HttpUtils.sendSuccess(exchange, target.toMap(false));
    }

    private void handleUpdateProfile(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        User updated = userService.updateProfile(user.getId(), body);
        HttpUtils.sendSuccess(exchange, updated.toMap(true));
    }

    private void handleUploadAvatar(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String imageData = (String) body.get("imageData");
        String fileName = (String) body.get("fileName");
        String url = userService.uploadAvatar(user.getId(), imageData, fileName);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("avatarUrl", url);
        HttpUtils.sendSuccess(exchange, resp);
    }

    private void handleAddEducation(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String school = (String) body.get("school");
        String degree = (String) body.get("degree");
        String field = (String) body.get("fieldOfStudy");
        int startYear = body.get("startYear") != null ? ((Number) body.get("startYear")).intValue() : 2022;
        int endYear = body.get("endYear") != null ? ((Number) body.get("endYear")).intValue() : 2026;

        userService.addEducation(user.getId(), school, degree, field, startYear, endYear);
        HttpUtils.sendSuccess(exchange, user.toMap(true));
    }

    private void handleAddProject(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String title = (String) body.get("title");
        String desc = (String) body.get("description");
        String repo = (String) body.get("githubRepoUrl");
        String live = (String) body.get("liveUrl");
        String tags = (String) body.get("tags");

        userService.addProject(user.getId(), title, desc, repo, live, tags);
        HttpUtils.sendSuccess(exchange, user.toMap(true));
    }

    private void handleImportGithub(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String username = (String) body.get("githubUsername");
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("GitHub username is required.");
        }

        List<Project> imported = userService.importGithubProjects(user.getId(), username.trim());
        List<Map<String, Object>> res = new ArrayList<>();
        for (Project p : imported) res.add(p.toMap());
        HttpUtils.sendSuccess(exchange, res);
    }

    private void handleSubmitCertificate(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String title = (String) body.get("title");
        String org = (String) body.get("issuingOrg");
        String date = (String) body.get("issueDate");
        String url = (String) body.get("credentialUrl");
        String proof = (String) body.get("proofDescription");

        Certificate cert = userService.submitCertificate(user.getId(), title, org, date, url, proof);
        HttpUtils.sendSuccess(exchange, cert.toMap());
    }

    private void handleGetPassport(HttpExchange exchange, Map<String, String> query) throws IOException {
        String uid = query.get("userId");
        if (uid == null) {
            User u = requireAuth(exchange);
            uid = u.getId();
        }
        Map<String, Object> passport = userService.getSkillPassport(uid);
        HttpUtils.sendSuccess(exchange, passport);
    }

    // ==================== SKILLS & QUIZZES ====================

    private void handleAddSkill(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String name = (String) body.get("skillName");
        String type = (String) body.get("skillType");
        String level = (String) body.get("proficiencyLevel");

        Skill s = skillService.addSkill(user.getId(), name, type, level);
        HttpUtils.sendSuccess(exchange, s.toMap());
    }

    private void handleBecomeMentor(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);

        List<String> skills = new ArrayList<>();
        Object skillsObj = body.get("skills");
        if (skillsObj instanceof List) {
            for (Object o : (List<?>) skillsObj) {
                if (o != null) skills.add(o.toString());
            }
        } else if (skillsObj instanceof String) {
            String s = (String) skillsObj;
            for (String part : s.split(",")) {
                if (!part.trim().isEmpty()) skills.add(part.trim());
            }
        }

        String intro = (String) body.get("mentorIntro");
        String level = (String) body.get("expertiseLevel");
        String formats = (String) body.get("availableFormats");
        String slots = (String) body.get("availableTimeSlots");
        int price = body.get("tierPrice") != null ? ((Number) body.get("tierPrice")).intValue() : 0;

        skillService.registerMentor(user.getId(), skills, intro, level, formats, slots, price);
        User updated = userService.getProfile(user.getId());
        HttpUtils.sendSuccess(exchange, updated.toMap(true));
    }

    private void handleDeleteSkill(HttpExchange exchange, Map<String, String> query) throws IOException {
        User user = requireAuth(exchange);
        String skillId = query.get("skillId");
        skillService.removeSkill(user.getId(), skillId);
        HttpUtils.sendSuccess(exchange, "Skill removed.");
    }

    private void handleSearchTeachers(HttpExchange exchange, Map<String, String> query) throws IOException {
        String q = query.get("q");
        String tier = query.get("tier");
        String minRatStr = query.get("minRating");
        Integer minRat = minRatStr != null ? Integer.parseInt(minRatStr) : null;

        List<Map<String, Object>> teachers = skillService.searchTeachers(q, tier, minRat);
        HttpUtils.sendSuccess(exchange, teachers);
    }

    private void handleGetQuiz(HttpExchange exchange, Map<String, String> query) throws IOException {
        String skill = query.get("skill");
        if (skill == null || skill.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill name parameter is required.");
        }
        List<QuizQuestion> questions = skillService.getQuizForSkill(skill.trim());
        List<Map<String, Object>> list = new ArrayList<>();
        for (QuizQuestion q : questions) {
            list.add(q.toMap(false)); // Do not leak answers to frontend!
        }
        HttpUtils.sendSuccess(exchange, list);
    }

    private void handleSubmitQuiz(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String skill = (String) body.get("skillName");
        Map<String, Object> rawAnswers = (Map<String, Object>) body.get("answers");

        Map<String, Integer> answers = new HashMap<>();
        if (rawAnswers != null) {
            for (Map.Entry<String, Object> e : rawAnswers.entrySet()) {
                answers.put(e.getKey(), ((Number) e.getValue()).intValue());
            }
        }

        Map<String, Object> result = skillService.submitQuiz(user.getId(), skill, answers);
        HttpUtils.sendSuccess(exchange, result);
    }

    // ==================== MATCHING ====================

    private void handleGetAllMatches(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> matches = matchingService.getAllMatches(user.getId());
        HttpUtils.sendSuccess(exchange, matches);
    }

    // ==================== CREDITS & LEDGER ====================

    private void handleBuyCredits(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        int amount = body.get("amount") != null ? ((Number) body.get("amount")).intValue() : 50;
        String method = (String) body.get("paymentMethod");
        String txRef = (String) body.get("txRef");

        Map<String, Object> res = creditService.buyCredits(user.getId(), amount, method, txRef);
        HttpUtils.sendSuccess(exchange, res);
    }

    private void handleGetLedger(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        List<CreditTransaction> ledger = creditService.getLedger(user.getId());
        List<Map<String, Object>> res = new ArrayList<>();
        for (CreditTransaction t : ledger) res.add(t.toMap());
        HttpUtils.sendSuccess(exchange, res);
    }

    private void handleGetPool(HttpExchange exchange) throws IOException {
        PayItForwardPool pool = HybridDataRepository.getInstance().getPayItForwardPool();
        HttpUtils.sendSuccess(exchange, pool.toMap());
    }

    private void handleDonatePool(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        int amount = body.get("amount") != null ? ((Number) body.get("amount")).intValue() : 10;
        String note = (String) body.get("note");

        creditService.donateToPool(user.getId(), amount, note != null ? note : "Campus learning support");
        HttpUtils.sendSuccess(exchange, "Donation successful! Thank you for paying it forward.");
    }

    private void handleClaimPool(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        int amount = body.get("amount") != null ? ((Number) body.get("amount")).intValue() : 20;
        String reason = (String) body.get("reason");

        boolean ok = creditService.claimFromPool(user.getId(), amount, reason != null ? reason : "Needing credits for session");
        if (ok) {
            HttpUtils.sendSuccess(exchange, "Granted " + amount + " credits from community pool!");
        } else {
            HttpUtils.sendError(exchange, 400, "Pool balance insufficient to fulfill grant request.");
        }
    }

    // ==================== SESSIONS ====================

    private void handleRequestSession(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        Session session = sessionService.createSession(user.getId(), body);
        HttpUtils.sendSuccess(exchange, session.toMap());
    }

    private void handleAcceptSession(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String sid = (String) body.get("sessionId");
        sessionService.acceptSession(user.getId(), sid);
        HttpUtils.sendSuccess(exchange, "Session accepted.");
    }

    private void handleRejectSession(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String sid = (String) body.get("sessionId");
        sessionService.rejectSession(user.getId(), sid);
        HttpUtils.sendSuccess(exchange, "Session rejected and credits refunded.");
    }

    private void handleCancelSession(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String sid = (String) body.get("sessionId");
        sessionService.cancelSession(user.getId(), sid);
        HttpUtils.sendSuccess(exchange, "Session cancelled and credits refunded.");
    }

    private void handleCompleteSession(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String sid = (String) body.get("sessionId");
        sessionService.completeSession(user.getId(), sid);
        HttpUtils.sendSuccess(exchange, "Session completed and escrow released to teacher!");
    }

    private void handleEnrollGroupClass(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String sid = (String) body.get("sessionId");
        sessionService.enrollGroupClass(user.getId(), sid);
        HttpUtils.sendSuccess(exchange, "Successfully enrolled in group masterclass.");
    }

    private void handleSubmitReview(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String sid = (String) body.get("sessionId");
        int rating = body.get("rating") != null ? ((Number) body.get("rating")).intValue() : 5;
        String text = (String) body.get("reviewText");

        Review r = sessionService.submitReview(user.getId(), sid, rating, text);
        HttpUtils.sendSuccess(exchange, r.toMap());
    }

    private void handleGetMySessions(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        List<Session> list = sessionService.getUserSessions(user.getId());
        List<Map<String, Object>> res = new ArrayList<>();
        for (Session s : list) res.add(s.toMap());
        HttpUtils.sendSuccess(exchange, res);
    }

    // ==================== COMMUNITY ====================

    private void handleSendMessage(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String recipientId = (String) body.get("receiverId");
        String content = (String) body.get("content");

        Message msg = communityService.sendMessage(user.getId(), recipientId, content);
        HttpUtils.sendSuccess(exchange, msg.toMap());
    }

    private void handleGetConversation(HttpExchange exchange, Map<String, String> query) throws IOException {
        User user = requireAuth(exchange);
        String peerId = query.get("peerId");
        if (peerId == null) throw new IllegalArgumentException("peerId required.");

        List<Message> msgs = communityService.getConversation(user.getId(), peerId);
        List<Map<String, Object>> res = new ArrayList<>();
        for (Message m : msgs) res.add(m.toMap());
        HttpUtils.sendSuccess(exchange, res);
    }

    private void handleGetNotifications(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        List<Notification> list = communityService.getNotifications(user.getId());
        List<Map<String, Object>> res = new ArrayList<>();
        for (Notification n : list) res.add(n.toMap());
        HttpUtils.sendSuccess(exchange, res);
    }

    private void handleReadNotification(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String nid = (String) body.get("notificationId");
        communityService.markNotificationRead(nid);
        HttpUtils.sendSuccess(exchange, "Notification marked read.");
    }

    private void handleGetHackathons(HttpExchange exchange) throws IOException {
        List<HackathonTeam> list = communityService.getAllHackathonTeams();
        List<Map<String, Object>> res = new ArrayList<>();
        for (HackathonTeam t : list) res.add(t.toMap());
        HttpUtils.sendSuccess(exchange, res);
    }

    private void handleCreateHackathon(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String title = (String) body.get("title");
        String hackName = (String) body.get("hackathonName");
        String desc = (String) body.get("description");
        String looking = (String) body.get("lookingForSkills");
        int max = body.get("maxMembers") != null ? ((Number) body.get("maxMembers")).intValue() : 4;

        HackathonTeam t = communityService.createHackathonTeam(user.getId(), title, hackName, desc, looking, max);
        HttpUtils.sendSuccess(exchange, t.toMap());
    }

    private void handleJoinHackathon(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String tid = (String) body.get("teamId");
        String role = (String) body.get("role");

        boolean ok = communityService.joinHackathonTeam(user.getId(), tid, role);
        if (ok) {
            HttpUtils.sendSuccess(exchange, "Joined hackathon team successfully!");
        } else {
            HttpUtils.sendError(exchange, 400, "Could not join team: Team might be full or you are already a member.");
        }
    }

    private void handleGetLeaderboard(HttpExchange exchange) throws IOException {
        List<Map<String, Object>> leaderboard = communityService.getLeaderboard();
        HttpUtils.sendSuccess(exchange, leaderboard);
    }

    private void handleGetKnowledge(HttpExchange exchange) throws IOException {
        List<KnowledgePost> posts = communityService.getKnowledgePosts();
        List<Map<String, Object>> res = new ArrayList<>();
        for (KnowledgePost p : posts) res.add(p.toMap());
        HttpUtils.sendSuccess(exchange, res);
    }

    private void handleCreateKnowledge(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String title = (String) body.get("title");
        String cat = (String) body.get("category");
        String tags = (String) body.get("tags");
        String content = (String) body.get("content");

        KnowledgePost kp = communityService.createKnowledgePost(user.getId(), title, cat, tags, content);
        HttpUtils.sendSuccess(exchange, kp.toMap());
    }

    private void handleUpvoteKnowledge(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String pid = (String) body.get("postId");
        KnowledgePost kp = communityService.toggleUpvoteKnowledge(user.getId(), pid);
        HttpUtils.sendSuccess(exchange, kp.toMap());
    }

    private void handleAnswerKnowledge(HttpExchange exchange) throws IOException {
        User user = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String pid = (String) body.get("postId");
        String answer = (String) body.get("answerText");

        communityService.addKnowledgeAnswer(user.getId(), pid, answer);
        HttpUtils.sendSuccess(exchange, "Answer added.");
    }

    private void handleGetRoadmaps(HttpExchange exchange) throws IOException {
        List<Roadmap> list = communityService.getRoadmaps();
        List<Map<String, Object>> res = new ArrayList<>();
        for (Roadmap r : list) res.add(r.toMap());
        HttpUtils.sendSuccess(exchange, res);
    }

    // ==================== ADMIN ====================

    private void handleAdminGetUsers(HttpExchange exchange) throws IOException {
        User admin = requireAuth(exchange);
        List<Map<String, Object>> users = adminService.getAllUsers(admin.getId());
        HttpUtils.sendSuccess(exchange, users);
    }

    private void handleAdminBanUser(HttpExchange exchange) throws IOException {
        User admin = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String targetId = (String) body.get("targetUserId");
        boolean banned = adminService.toggleUserBan(admin.getId(), targetId);
        HttpUtils.sendSuccess(exchange, Map.of("userId", targetId, "isBanned", banned));
    }

    private void handleAdminGetCertificates(HttpExchange exchange) throws IOException {
        User admin = requireAuth(exchange);
        List<Map<String, Object>> pending = adminService.getPendingCertificates(admin.getId());
        HttpUtils.sendSuccess(exchange, pending);
    }

    private void handleAdminReviewCertificate(HttpExchange exchange) throws IOException {
        User admin = requireAuth(exchange);
        Map<String, Object> body = HttpUtils.readJsonBody(exchange);
        String certId = (String) body.get("certificateId");
        String status = (String) body.get("status");
        String notes = (String) body.get("adminNotes");

        adminService.reviewCertificate(admin.getId(), certId, status, notes);
        HttpUtils.sendSuccess(exchange, "Certificate " + status.toLowerCase() + " successfully.");
    }

    private void handleAdminStats(HttpExchange exchange) throws IOException {
        User admin = requireAuth(exchange);
        Map<String, Object> stats = adminService.getPlatformStats(admin.getId());
        HttpUtils.sendSuccess(exchange, stats);
    }

    private void handleAdminDbReconnect(HttpExchange exchange) throws IOException {
        User admin = requireAuth(exchange);
        if (!"ADMIN".equalsIgnoreCase(admin.getRole())) throw new SecurityException("Admin required.");

        DatabaseManager.getInstance().initConnection();
        boolean connected = DatabaseManager.getInstance().isConnected();
        String msg = DatabaseManager.getInstance().getStatusMessage();
        if (connected) {
            HybridDataRepository.getInstance().syncToDatabase();
        }
        HttpUtils.sendSuccess(exchange, Map.of("connected", connected, "message", msg));
    }
}
