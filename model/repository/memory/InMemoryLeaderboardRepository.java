package com.wordy.server.model.repository.memory;

import com.wordy.server.model.entity.LongestWordEntry;
import com.wordy.server.model.repository.LeaderboardRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryLeaderboardRepository implements LeaderboardRepository {

    private static final InMemoryLeaderboardRepository INSTANCE = new InMemoryLeaderboardRepository();

    private final List<LongestWordEntry> entries = new CopyOnWriteArrayList<>();

    public static InMemoryLeaderboardRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public void insertWord(String username, String word) {
        if (username == null || word == null || word.isBlank()) {
            return;
        }
        entries.add(new LongestWordEntry(username, word.trim().toLowerCase()));
        trimToTop(5);
    }

    @Override
    public List<LongestWordEntry> getTopWords(int limit) {
        List<LongestWordEntry> sorted = new ArrayList<>(entries);
        sorted.sort(Comparator.comparingInt((LongestWordEntry e) -> e.word().length()).reversed()
                .thenComparing(LongestWordEntry::username));
        if (sorted.size() <= limit) {
            return sorted;
        }
        return sorted.subList(0, limit);
    }

    private void trimToTop(int limit) {
        List<LongestWordEntry> top = getTopWords(limit);
        entries.clear();
        entries.addAll(top);
    }
}
