package com.wordy.server.model.repository;

import com.wordy.server.model.entity.LongestWordEntry;

import java.util.List;

public interface LeaderboardRepository {

    void insertWord(String username, String word);

    List<LongestWordEntry> getTopWords(int limit);
}
