package com.skillswap.service.matching;

import com.skillswap.model.Skill;
import com.skillswap.model.User;
import java.util.*;

/**
 * Chain Matching: Solves barter deadlocks by identifying 3-party cyclic swap rings (A -> B -> C -> A).
 * Uses Directed Graph Cycle Detection algorithm.
 */
public class ChainMatchingStrategy implements MatchingStrategy {

    public static class TradeEdge {
        public User teacher;
        public User learner;
        public String skill;

        public TradeEdge(User teacher, User learner, String skill) {
            this.teacher = teacher;
            this.learner = learner;
            this.skill = skill;
        }
    }

    @Override
    public List<MatchResult> findMatches(User targetUser, List<User> allUsers) {
        List<MatchResult> chainMatches = new ArrayList<>();
        if (targetUser == null || allUsers.size() < 3) return chainMatches;

        // Build directed edges: Teacher -> Learner (Teacher has skill that Learner wants)
        Map<String, List<TradeEdge>> outgoingEdges = new HashMap<>();
        for (User u : allUsers) {
            outgoingEdges.put(u.getId(), new ArrayList<>());
        }

        for (User teacher : allUsers) {
            if (teacher.isBanned()) continue;
            List<Skill> teaches = getSkillsByType(teacher, "TEACH");

            for (User learner : allUsers) {
                if (teacher.getId().equals(learner.getId()) || learner.isBanned()) continue;
                List<Skill> learns = getSkillsByType(learner, "LEARN");

                Skill match = findMatchingSkill(teaches, learns);
                if (match != null) {
                    TradeEdge edge = new TradeEdge(teacher, learner, match.getSkillName());
                    outgoingEdges.get(teacher.getId()).add(edge);
                }
            }
        }

        // Look for 3-cycles involving targetUser: A -> B -> C -> A
        String targetId = targetUser.getId();
        List<TradeEdge> edgesFromA = outgoingEdges.getOrDefault(targetId, Collections.emptyList());

        Set<String> seenCycles = new HashSet<>();

        for (TradeEdge aToB : edgesFromA) {
            User userB = aToB.learner;
            if (userB.getId().equals(targetId)) continue;

            List<TradeEdge> edgesFromB = outgoingEdges.getOrDefault(userB.getId(), Collections.emptyList());
            for (TradeEdge bToC : edgesFromB) {
                User userC = bToC.learner;
                if (userC.getId().equals(targetId) || userC.getId().equals(userB.getId())) continue;

                List<TradeEdge> edgesFromC = outgoingEdges.getOrDefault(userC.getId(), Collections.emptyList());
                for (TradeEdge cToA : edgesFromC) {
                    if (cToA.learner.getId().equals(targetId)) {
                        // Found a valid 3-cycle: A -> B -> C -> A!
                        String cycleKey = userB.getId() + ":" + userC.getId();
                        if (seenCycles.contains(cycleKey)) continue;
                        seenCycles.add(cycleKey);

                        MatchResult result = new MatchResult("CHAIN", 92,
                            "3-Way Cyclic Swap! " + targetUser.getName() + " teaches " + aToB.skill + " to " + userB.getName() +
                            ", " + userB.getName() + " teaches " + bToC.skill + " to " + userC.getName() +
                            ", and " + userC.getName() + " teaches " + cToA.skill + " to " + targetUser.getName());

                        Map<String, Object> details = new LinkedHashMap<>();
                        details.put("cycleLength", 3);
                        details.put("step1", targetUser.getName() + " teaches " + aToB.skill + " -> " + userB.getName());
                        details.put("step2", userB.getName() + " teaches " + bToC.skill + " -> " + userC.getName());
                        details.put("step3", userC.getName() + " teaches " + cToA.skill + " -> " + targetUser.getName());

                        List<Map<String, Object>> ringNodes = new ArrayList<>();

                        Map<String, Object> n1 = targetUser.toMap(false);
                        n1.put("teachesSkill", aToB.skill);
                        n1.put("receivesSkill", cToA.skill);
                        ringNodes.add(n1);

                        Map<String, Object> n2 = userB.toMap(false);
                        n2.put("teachesSkill", bToC.skill);
                        n2.put("receivesSkill", aToB.skill);
                        ringNodes.add(n2);

                        Map<String, Object> n3 = userC.toMap(false);
                        n3.put("teachesSkill", cToA.skill);
                        n3.put("receivesSkill", bToC.skill);
                        ringNodes.add(n3);

                        details.put("ring", ringNodes);
                        result.setDetails(details);
                        result.setParticipants(ringNodes);

                        chainMatches.add(result);
                    }
                }
            }
        }

        return chainMatches;
    }

    private List<Skill> getSkillsByType(User user, String type) {
        List<Skill> list = new ArrayList<>();
        if (user.getSkills() != null) {
            for (Skill s : user.getSkills()) {
                if (type.equalsIgnoreCase(s.getSkillType())) {
                    list.add(s);
                }
            }
        }
        return list;
    }

    private Skill findMatchingSkill(List<Skill> teacherSkills, List<Skill> learnerSkills) {
        for (Skill t : teacherSkills) {
            for (Skill l : learnerSkills) {
                if (t.getSkillName().equalsIgnoreCase(l.getSkillName())) {
                    return t;
                }
            }
        }
        return null;
    }
}

