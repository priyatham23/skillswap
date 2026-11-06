package com.skillswap.service.matching;

import com.skillswap.model.Skill;
import com.skillswap.model.User;
import java.util.*;

public class DirectSwapStrategy implements MatchingStrategy {

    @Override
    public List<MatchResult> findMatches(User targetUser, List<User> allUsers) {
        List<MatchResult> matches = new ArrayList<>();
        if (targetUser == null) return matches;

        List<Skill> myTeaches = getSkillsByType(targetUser, "TEACH");
        List<Skill> myLearns = getSkillsByType(targetUser, "LEARN");

        if (myTeaches.isEmpty() || myLearns.isEmpty()) {
            return matches;
        }

        for (User peer : allUsers) {
            if (peer.getId().equals(targetUser.getId()) || peer.isBanned()) {
                continue;
            }

            List<Skill> peerTeaches = getSkillsByType(peer, "TEACH");
            List<Skill> peerLearns = getSkillsByType(peer, "LEARN");

            // Check if I teach something Peer learns
            Skill iTeachPeerWants = findSkillMatch(myTeaches, peerLearns);
            // Check if Peer teaches something I learn
            Skill peerTeachesIWants = findSkillMatch(peerTeaches, myLearns);

            if (iTeachPeerWants != null && peerTeachesIWants != null) {
                int score = calculateMatchScore(targetUser, peer, iTeachPeerWants, peerTeachesIWants);

                MatchResult res = new MatchResult("DIRECT", score,
                    "Perfect Direct Swap! You teach " + iTeachPeerWants.getSkillName() +
                    " and " + peer.getName() + " teaches " + peerTeachesIWants.getSkillName());

                Map<String, Object> details = new LinkedHashMap<>();
                details.put("targetUserId", targetUser.getId());
                details.put("peerUserId", peer.getId());
                details.put("peerName", peer.getName());
                details.put("peerAvatar", peer.getAvatarUrl());
                details.put("peerHeadline", peer.getHeadline());
                details.put("peerTier", peer.getTier());
                details.put("peerCollege", peer.getCollege());
                details.put("youTeach", iTeachPeerWants.getSkillName());
                details.put("peerTeaches", peerTeachesIWants.getSkillName());
                details.put("peerVerified", peerTeachesIWants.isVerified());

                List<Map<String, Object>> pList = new ArrayList<>();
                pList.add(targetUser.toMap(false));
                pList.add(peer.toMap(false));
                res.setParticipants(pList);
                res.setDetails(details);

                matches.add(res);
            }
        }

        // Sort by match score descending
        matches.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        return matches;
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

    private Skill findSkillMatch(List<Skill> provides, List<Skill> requests) {
        for (Skill p : provides) {
            for (Skill r : requests) {
                if (p.getSkillName().equalsIgnoreCase(r.getSkillName())) {
                    return p;
                }
            }
        }
        return null;
    }

    private int calculateMatchScore(User me, User peer, Skill myTeach, Skill peerTeach) {
        int score = 40; // Base score for 100% reciprocal skill matching

        // 1. Level compatibility (up to 25 pts)
        int peerLevelWeight = getLevelWeight(peerTeach.getProficiencyLevel());
        int myLevelWeight = getLevelWeight(myTeach.getProficiencyLevel());
        score += Math.min(25, (peerLevelWeight + myLevelWeight) * 3);

        // 2. Peer Tier & Verification (up to 20 pts)
        if ("Pro".equalsIgnoreCase(peer.getTier())) score += 20;
        else if ("Intermediate".equalsIgnoreCase(peer.getTier())) score += 15;
        else score += 10;

        if (peerTeach.isVerified()) score += 5;

        // 3. Activity / Streak (up to 10 pts)
        long hoursSinceActive = (System.currentTimeMillis() - peer.getLastActive()) / (1000 * 3600);
        if (hoursSinceActive < 24) score += 10;
        else if (hoursSinceActive < 72) score += 6;
        else score += 3;

        return Math.min(100, Math.max(50, score));
    }

    private int getLevelWeight(String level) {
        if (level == null) return 1;
        switch (level.toLowerCase()) {
            case "expert": return 4;
            case "advanced": return 3;
            case "intermediate": return 2;
            case "beginner":
            default: return 1;
        }
    }
}

