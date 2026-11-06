package com.skillswap.service;

import com.skillswap.model.User;
import com.skillswap.repository.DataRepository;
import com.skillswap.service.matching.ChainMatchingStrategy;
import com.skillswap.service.matching.DirectSwapStrategy;
import com.skillswap.service.matching.MatchResult;

import java.util.*;

public class MatchingService {
    private final DataRepository repository;
    private final DirectSwapStrategy directSwapStrategy;
    private final ChainMatchingStrategy chainMatchingStrategy;

    public MatchingService(DataRepository repository) {
        this.repository = repository;
        this.directSwapStrategy = new DirectSwapStrategy();
        this.chainMatchingStrategy = new ChainMatchingStrategy();
    }

    public List<MatchResult> getDirectSwapMatches(String userId) {
        User user = repository.findUserById(userId);
        if (user == null) return Collections.emptyList();
        List<User> allUsers = repository.findAllUsers();
        return directSwapStrategy.findMatches(user, allUsers);
    }

    public List<MatchResult> getChainMatches(String userId) {
        User user = repository.findUserById(userId);
        if (user == null) return Collections.emptyList();
        List<User> allUsers = repository.findAllUsers();
        return chainMatchingStrategy.findMatches(user, allUsers);
    }

    public Map<String, Object> getAllMatches(String userId) {
        Map<String, Object> res = new LinkedHashMap<>();
        List<MatchResult> directs = getDirectSwapMatches(userId);
        List<MatchResult> chains = getChainMatches(userId);

        List<Map<String, Object>> directMaps = new ArrayList<>();
        for (MatchResult m : directs) directMaps.add(m.toMap());

        List<Map<String, Object>> chainMaps = new ArrayList<>();
        for (MatchResult m : chains) chainMaps.add(m.toMap());

        res.put("directSwaps", directMaps);
        res.put("chainSwaps", chainMaps);
        res.put("totalDirectCount", directs.size());
        res.put("totalChainCount", chains.size());
        return res;
    }
}

