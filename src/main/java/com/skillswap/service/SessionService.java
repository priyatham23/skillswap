package com.skillswap.service;

import com.skillswap.model.*;
import com.skillswap.repository.DataRepository;
import com.skillswap.util.SecurityUtils;

import java.util.*;

public class SessionService {
    private final DataRepository repository;
    private final CreditService creditService;

    public SessionService(DataRepository repository, CreditService creditService) {
        this.repository = repository;
        this.creditService = creditService;
    }

    public synchronized Session createSession(String learnerId, Map<String, Object> data) {
        String sessionType = (String) data.get("sessionType");
        if (sessionType == null) sessionType = "DIRECT_SWAP";

        String teacherId = (String) data.get("teacherId");
        String skillTopic = (String) data.get("skillTopic");
        String description = (String) data.get("description");
        String scheduledTime = (String) data.get("scheduledTime");
        if (scheduledTime == null || scheduledTime.trim().isEmpty()) {
            scheduledTime = "Flexible / As Agreed";
        }

        User teacher = repository.findUserById(teacherId);
        User learner = repository.findUserById(learnerId);
        if (teacher == null || learner == null) {
            throw new IllegalArgumentException("Invalid teacher or learner ID.");
        }

        String sessionId = "sess-" + SecurityUtils.generateId().substring(0, 8);
        Session session;

        switch (sessionType.toUpperCase()) {
            case "DOUBT_SPRINT":
                String urgency = (String) data.get("urgencyLevel");
                String error = (String) data.get("errorSnippet");
                session = new DoubtSprintSession(sessionId, teacherId, learnerId, skillTopic, description, urgency, error);
                break;

            case "MOCK_INTERVIEW":
                String interviewType = (String) data.get("interviewType");
                String targetCompany = (String) data.get("targetCompany");
                String targetRole = (String) data.get("targetRole");
                session = new MockInterviewSession(sessionId, teacherId, learnerId, skillTopic, description, scheduledTime,
                    interviewType != null ? interviewType : "Technical DSA",
                    targetCompany != null ? targetCompany : "Tech",
                    targetRole != null ? targetRole : "Software Engineer");
                break;

            case "CODE_REVIEW":
                String repo = (String) data.get("repoUrl");
                String pr = (String) data.get("pullRequestUrl");
                String lang = (String) data.get("programmingLanguage");
                session = new CodeReviewSession(sessionId, teacherId, learnerId, skillTopic, description, repo, pr, lang != null ? lang : "General");
                break;

            case "GROUP_CLASS":
                int seats = data.get("totalSeats") != null ? ((Number) data.get("totalSeats")).intValue() : 10;
                int price = data.get("pricePerSeat") != null ? ((Number) data.get("pricePerSeat")).intValue() : 15;
                session = new GroupClassSession(sessionId, teacherId, skillTopic, description, scheduledTime, seats, price);
                session.setStatus(SessionStatus.ACCEPTED); // Group class hosted by teacher is immediately active
                repository.saveSession(session);
                return session;

            case "BUILD_TOGETHER":
                String bRepo = (String) data.get("projectRepoUrl");
                String stack = (String) data.get("techStack");
                String milestone = (String) data.get("milestoneGoal");
                session = new BuildTogetherSession(sessionId, teacherId, learnerId, skillTopic, description, scheduledTime,
                    bRepo != null ? bRepo : "https://github.com/project", stack, milestone);
                break;

            case "DIRECT_SWAP":
            default:
                String reciprocal = (String) data.get("reciprocalSkill");
                int cost = teacher.getTierPrice();
                session = new DirectSwapSession(sessionId, teacherId, learnerId, skillTopic, description, scheduledTime, 60, cost, reciprocal);
                break;
        }

        session.validateSessionCreation();

        // Hold Escrow from learner
        int escrowRequired = session.calculateEscrow();
        creditService.holdEscrow(learnerId, escrowRequired, sessionId);

        repository.saveSession(session);

        // Send notification to Teacher
        repository.saveNotification(new Notification(
            SecurityUtils.generateId(),
            teacherId,
            "New Swap Session Request!",
            learner.getName() + " requested a " + session.getSessionType() + " for '" + skillTopic + "'.",
            "#sessions"
        ));

        return session;
    }

    public synchronized void acceptSession(String teacherId, String sessionId) {
        Session session = repository.findSessionById(sessionId);
        if (session == null) throw new IllegalArgumentException("Session not found.");
        if (!teacherId.equals(session.getTeacherId())) {
            throw new IllegalStateException("Only the designated teacher can accept this session.");
        }

        session.accept();
        repository.saveSession(session);

        repository.saveNotification(new Notification(
            SecurityUtils.generateId(),
            session.getLearnerId(),
            "Swap Request Accepted!",
            "Your session for '" + session.getSkillTopic() + "' was accepted. Join via the meeting link at your scheduled time.",
            "#sessions"
        ));
    }

    public synchronized void rejectSession(String teacherId, String sessionId) {
        Session session = repository.findSessionById(sessionId);
        if (session == null) throw new IllegalArgumentException("Session not found.");
        if (!teacherId.equals(session.getTeacherId())) {
            throw new IllegalStateException("Only the designated teacher can reject this session.");
        }

        session.reject();
        // Refund escrow to learner
        creditService.refundEscrow(session.getLearnerId(), session.calculateEscrow(), sessionId);
        repository.saveSession(session);

        repository.saveNotification(new Notification(
            SecurityUtils.generateId(),
            session.getLearnerId(),
            "Swap Request Declined",
            "Teacher declined your session request. Your held credits have been refunded to your wallet.",
            "#sessions"
        ));
    }

    public synchronized void cancelSession(String userId, String sessionId) {
        Session session = repository.findSessionById(sessionId);
        if (session == null) throw new IllegalArgumentException("Session not found.");
        if (!userId.equals(session.getTeacherId()) && !userId.equals(session.getLearnerId())) {
            throw new IllegalStateException("Not authorized to cancel this session.");
        }

        session.cancel();
        // Refund escrow if not already completed
        creditService.refundEscrow(session.getLearnerId(), session.calculateEscrow(), sessionId);
        repository.saveSession(session);

        String otherUser = userId.equals(session.getTeacherId()) ? session.getLearnerId() : session.getTeacherId();
        repository.saveNotification(new Notification(
            SecurityUtils.generateId(),
            otherUser,
            "Session Cancelled",
            "The session for '" + session.getSkillTopic() + "' was cancelled.",
            "#sessions"
        ));
    }

    public synchronized void completeSession(String userId, String sessionId) {
        Session session = repository.findSessionById(sessionId);
        if (session == null) throw new IllegalArgumentException("Session not found.");
        if (!userId.equals(session.getTeacherId()) && !userId.equals(session.getLearnerId())) {
            throw new IllegalStateException("Not authorized to complete this session.");
        }

        session.complete();

        // Release escrow credits to teacher
        creditService.releaseEscrow(session.getLearnerId(), session.getTeacherId(), session.calculateEscrow(), sessionId);

        // Update streaks
        User teacher = repository.findUserById(session.getTeacherId());
        User learner = repository.findUserById(session.getLearnerId());
        if (teacher != null) {
            teacher.setStreakDays(teacher.getStreakDays() + 1);
            teacher.setLastActive(System.currentTimeMillis());
            repository.saveUser(teacher);
        }
        if (learner != null) {
            learner.setStreakDays(learner.getStreakDays() + 1);
            learner.setLastActive(System.currentTimeMillis());
            repository.saveUser(learner);
        }

        repository.saveSession(session);

        // Prompt review
        repository.saveNotification(new Notification(
            SecurityUtils.generateId(),
            session.getLearnerId(),
            "Session Completed! Leave a Review",
            "Please rate your learning experience with " + (teacher != null ? teacher.getName() : "your teacher") + ".",
            "#sessions"
        ));
    }

    public synchronized void enrollGroupClass(String learnerId, String sessionId) {
        Session session = repository.findSessionById(sessionId);
        if (session == null || !(session instanceof GroupClassSession)) {
            throw new IllegalArgumentException("Valid group class not found.");
        }

        GroupClassSession g = (GroupClassSession) session;
        boolean added = g.enrollLearner(learnerId);
        if (!added) {
            throw new IllegalStateException("Already enrolled or group class is full.");
        }

        // Hold escrow for this learner's seat
        creditService.holdEscrow(learnerId, g.getPricePerSeat(), sessionId);
        repository.saveSession(g);

        repository.saveNotification(new Notification(
            SecurityUtils.generateId(),
            g.getTeacherId(),
            "New Group Class Enrollment!",
            "A learner booked a seat in your masterclass '" + g.getSkillTopic() + "'.",
            "#sessions"
        ));
    }

    public synchronized Review submitReview(String learnerId, String sessionId, int rating, String reviewText) {
        Session session = repository.findSessionById(sessionId);
        if (session == null) throw new IllegalArgumentException("Session not found.");

        User learner = repository.findUserById(learnerId);
        String learnerName = learner != null ? learner.getName() : "Learner";

        Review r = new Review(
            SecurityUtils.generateId(),
            sessionId,
            session.getTeacherId(),
            learnerId,
            learnerName,
            Math.max(1, Math.min(5, rating)),
            reviewText
        );
        repository.saveReview(r);

        // Recalculate teacher's tier
        User teacher = repository.findUserById(session.getTeacherId());
        if (teacher != null) {
            List<Review> allReviews = repository.findReviewsByTeacherId(teacher.getId());
            double sum = 0;
            for (Review rev : allReviews) sum += rev.getRating();
            double avg = sum / allReviews.size();

            List<Session> teacherSessions = repository.findSessionsByUserId(teacher.getId());
            int completedCount = 0;
            for (Session s : teacherSessions) {
                if (s.getStatus() == SessionStatus.COMPLETED && teacher.getId().equals(s.getTeacherId())) {
                    completedCount++;
                }
            }

            teacher.recalculateTier(completedCount, avg);
            repository.saveUser(teacher);
        }

        return r;
    }

    public List<Session> getUserSessions(String userId) {
        return repository.findSessionsByUserId(userId);
    }
}

