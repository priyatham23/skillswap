package com.skillswap.service.matching;

import com.skillswap.model.User;
import java.util.List;

public interface MatchingStrategy {
    List<MatchResult> findMatches(User targetUser, List<User> allUsers);
}

