package com.skillswap.repository;

import com.skillswap.model.*;
import java.util.List;

public interface DataRepository {
    // User Operations
    void saveUser(User user);
    User findUserById(String id);
    User findUserByEmail(String email);
    List<User> findAllUsers();
    void deleteUser(String id);

    // Session Operations
    void saveSession(Session session);
    Session findSessionById(String id);
    List<Session> findSessionsByUserId(String userId);
    List<Session> findAllSessions();

    // Credit Ledger Operations
    void logCreditTransaction(CreditTransaction transaction);
    List<CreditTransaction> findTransactionsByUserId(String userId);

    // Review Operations
    void saveReview(Review review);
    List<Review> findReviewsByTeacherId(String teacherId);

    // Messaging & Notifications
    void saveMessage(Message message);
    List<Message> findMessagesBetween(String user1, String user2);
    void saveNotification(Notification notification);
    List<Notification> findNotificationsByUserId(String userId);
    void markNotificationAsRead(String notificationId);

    // Hackathon Teams
    void saveHackathonTeam(HackathonTeam team);
    List<HackathonTeam> findAllHackathonTeams();
    HackathonTeam findHackathonTeamById(String id);

    // Quizzes & Skills
    List<QuizQuestion> findQuizQuestionsBySkill(String skillName);
    void saveQuizQuestion(QuizQuestion question);

    // Roadmaps
    List<Roadmap> findAllRoadmaps();
    Roadmap findRoadmapById(String id);

    // Knowledge Base
    void saveKnowledgePost(KnowledgePost post);
    List<KnowledgePost> findAllKnowledgePosts();
    KnowledgePost findKnowledgePostById(String id);

    // Pay It Forward Pool
    PayItForwardPool getPayItForwardPool();

    // Persistence & DB Status
    boolean isMySqlConnected();
    String getDatabaseStatus();
    void syncToDatabase();
}

