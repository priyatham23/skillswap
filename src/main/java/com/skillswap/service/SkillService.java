package com.skillswap.service;

import com.skillswap.model.*;
import com.skillswap.repository.DataRepository;
import com.skillswap.util.SecurityUtils;

import java.util.*;

public class SkillService {
    private final DataRepository repository;

    public SkillService(DataRepository repository) {
        this.repository = repository;
    }

    public Skill addSkill(String userId, String skillName, String skillType, String level) {
        User user = repository.findUserById(userId);
        if (user == null) throw new IllegalArgumentException("User not found.");

        if (skillName == null || skillName.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill name cannot be empty.");
        }
        if (skillType == null || (!skillType.equalsIgnoreCase("TEACH") && !skillType.equalsIgnoreCase("LEARN"))) {
            throw new IllegalArgumentException("Skill type must be TEACH or LEARN.");
        }

        // Check if skill already exists for this type
        for (Skill s : user.getSkills()) {
            if (s.getSkillName().equalsIgnoreCase(skillName.trim()) && s.getSkillType().equalsIgnoreCase(skillType)) {
                s.setProficiencyLevel(level != null ? level : "Intermediate");
                repository.saveUser(user);
                return s;
            }
        }

        Skill skill = new Skill(
            SecurityUtils.generateId(),
            skillName.trim(),
            skillType.toUpperCase(),
            level != null ? level : "Intermediate",
            false,
            0
        );

        user.getSkills().add(skill);
        repository.saveUser(user);
        return skill;
    }

    public void removeSkill(String userId, String skillId) {
        User user = repository.findUserById(userId);
        if (user == null) throw new IllegalArgumentException("User not found.");

        user.getSkills().removeIf(s -> s.getId().equals(skillId));
        repository.saveUser(user);
    }

    public void registerMentor(String userId, List<String> skills, String intro, String level, String formats, String slots, int price) {
        User user = repository.findUserById(userId);
        if (user == null) throw new IllegalArgumentException("User not found.");

        if (intro != null && !intro.trim().isEmpty()) user.setMentorIntro(intro.trim());
        if (level != null && !level.trim().isEmpty()) {
            user.setExpertiseLevel(level.trim());
            user.setTier(level.trim());
        }
        if (formats != null && !formats.trim().isEmpty()) user.setAvailableFormats(formats.trim());
        if (slots != null && !slots.trim().isEmpty()) user.setAvailableTimeSlots(slots.trim());
        if (price > 0) user.setTierPrice(price);

        if (skills != null) {
            for (String sk : skills) {
                if (sk != null && !sk.trim().isEmpty()) {
                    addSkill(userId, sk.trim(), "TEACH", level != null ? level : "Intermediate");
                }
            }
        }
        repository.saveUser(user);
    }

    public List<QuizQuestion> getQuizForSkill(String skillName) {
        List<QuizQuestion> questions = repository.findQuizQuestionsBySkill(skillName);
        if (questions.isEmpty()) {
            // Generate standard evaluation quiz questions dynamically
            questions = generateDefaultQuiz(skillName);
        }
        return questions;
    }

    private List<QuizQuestion> generateDefaultQuiz(String skillName) {
        List<QuizQuestion> list = new ArrayList<>();
        list.add(new QuizQuestion(SecurityUtils.generateId(), skillName,
            "What is the primary architectural principle of modern " + skillName + " development?",
            Arrays.asList("Separation of concerns and modularity", "Global mutable state", "Tight procedural coupling", "Monolithic single files"),
            0, "Modularity and separation of concerns improve maintainability and testability."));
        list.add(new QuizQuestion(SecurityUtils.generateId(), skillName,
            "When optimizing performance in " + skillName + ", what is the first recommended step?",
            Arrays.asList("Profiling and identifying actual bottlenecks", "Prematurely rewriting in assembly", "Disabling error logging", "Increasing hardware blindly"),
            0, "Profiling provides concrete metrics on memory and CPU consumption before refactoring."));
        list.add(new QuizQuestion(SecurityUtils.generateId(), skillName,
            "How should asynchronous workflows or errors be properly handled in " + skillName + "?",
            Arrays.asList("Explicit error handling, timeouts, and resource release", "Ignoring uncaught exceptions", "Infinite blocking loops", "Terminating the process abruptly"),
            0, "Robust error handling ensures fail-safe recovery and prevents resource leakage."));
        list.add(new QuizQuestion(SecurityUtils.generateId(), skillName,
            "What best practice ensures robust testability in " + skillName + "?",
            Arrays.asList("Dependency Injection and isolated unit testing", "Hardcoding external database connections", "Testing only in production", "Removing automated assertions"),
            0, "Dependency injection decouples implementations from interfaces, enabling deterministic mocks."));
        return list;
    }

    public Map<String, Object> submitQuiz(String userId, String skillName, Map<String, Integer> answers) {
        User user = repository.findUserById(userId);
        if (user == null) throw new IllegalArgumentException("User not found.");

        List<QuizQuestion> questions = getQuizForSkill(skillName);
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("No quiz available for " + skillName);
        }

        int correctCount = 0;
        List<Map<String, Object>> reviewDetails = new ArrayList<>();

        for (QuizQuestion q : questions) {
            Integer chosen = answers.get(q.getId());
            boolean isCorrect = chosen != null && chosen == q.getCorrectOptionIndex();
            if (isCorrect) correctCount++;

            Map<String, Object> r = new LinkedHashMap<>();
            r.put("questionId", q.getId());
            r.put("question", q.getQuestion());
            r.put("chosenOption", chosen);
            r.put("correctOption", q.getCorrectOptionIndex());
            r.put("isCorrect", isCorrect);
            r.put("explanation", q.getExplanation());
            reviewDetails.add(r);
        }

        int scorePercentage = (int) Math.round(((double) correctCount / questions.size()) * 100);
        boolean passed = scorePercentage >= 70;

        // Update skill in user's profile
        for (Skill s : user.getSkills()) {
            if (s.getSkillName().equalsIgnoreCase(skillName) && "TEACH".equalsIgnoreCase(s.getSkillType())) {
                s.setQuizScore(scorePercentage);
                if (passed) {
                    s.setVerified(true);
                }
            }
        }

        // Award verification bonus if passed
        if (passed) {
            user.addCredits(20);
            repository.logCreditTransaction(new CreditTransaction(
                SecurityUtils.generateId(),
                userId,
                "QUIZ_VERIFICATION_BONUS",
                20,
                user.getCredits(),
                "Skill Test Passed: Verified Badge in " + skillName,
                "QUIZ-" + skillName
            ));
            repository.saveNotification(new Notification(
                SecurityUtils.generateId(),
                userId,
                "Verified Skill Badge Unlocked!",
                "Congratulations! You passed the " + skillName + " assessment with " + scorePercentage + "% and earned 20 bonus credits.",
                "#profile"
            ));
        }

        repository.saveUser(user);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("skillName", skillName);
        result.put("scorePercentage", scorePercentage);
        result.put("correctCount", correctCount);
        result.put("totalQuestions", questions.size());
        result.put("passed", passed);
        result.put("verifiedBadgeAwarded", passed);
        result.put("review", reviewDetails);
        return result;
    }

    public List<Map<String, Object>> searchTeachers(String query, String tierFilter, Integer minRating) {
        List<Map<String, Object>> results = new ArrayList<>();
        List<User> users = repository.findAllUsers();

        for (User u : users) {
            if (u.isBanned()) continue;
            if (tierFilter != null && !tierFilter.isEmpty() && !tierFilter.equalsIgnoreCase("ALL")) {
                if (!u.getTier().equalsIgnoreCase(tierFilter)) continue;
            }

            for (Skill s : u.getSkills()) {
                if (!"TEACH".equalsIgnoreCase(s.getSkillType())) continue;

                boolean matchesQuery = query == null || query.trim().isEmpty() ||
                    s.getSkillName().toLowerCase().contains(query.toLowerCase()) ||
                    u.getName().toLowerCase().contains(query.toLowerCase()) ||
                    (u.getCollege() != null && u.getCollege().toLowerCase().contains(query.toLowerCase()));

                if (matchesQuery) {
                    List<Review> reviews = repository.findReviewsByTeacherId(u.getId());
                    double avgRating = 5.0;
                    if (!reviews.isEmpty()) {
                        double sum = 0;
                        for (Review r : reviews) sum += r.getRating();
                        avgRating = Math.round((sum / reviews.size()) * 10.0) / 10.0;
                    }

                    if (minRating != null && avgRating < minRating) continue;

                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("teacher", u.toMap(false));
                    item.put("skill", s.toMap());
                    item.put("averageRating", avgRating);
                    item.put("reviewsCount", reviews.size());
                    item.put("tier", u.getTier());
                    item.put("tierPrice", u.getTierPrice());

                    results.add(item);
                    break;
                }
            }
        }

        // Rank by Tier (Pro -> Intermediate -> Beginner) and Rating
        results.sort((a, b) -> {
            String tA = (String) a.get("tier");
            String tB = (String) b.get("tier");
            int weightA = "Pro".equalsIgnoreCase(tA) ? 3 : ("Intermediate".equalsIgnoreCase(tA) ? 2 : 1);
            int weightB = "Pro".equalsIgnoreCase(tB) ? 3 : ("Intermediate".equalsIgnoreCase(tB) ? 2 : 1);
            if (weightA != weightB) return Integer.compare(weightB, weightA);
            return Double.compare((Double) b.get("averageRating"), (Double) a.get("averageRating"));
        });

        return results;
    }
}

