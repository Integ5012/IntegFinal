package com.wordy.server.store;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-memory top-5 longest words leaderboard.
 */
public class InMemoryLeaderboardStore {

    private static final int MAX_ENTRIES = 5;

    public record WordEntry(String username, String word) {}

    private static final InMemoryLeaderboardStore INSTANCE = new InMemoryLeaderboardStore();

    private final List<WordEntry> entries = new CopyOnWriteArrayList<>();

    public static InMemoryLeaderboardStore getInstance() {
        return INSTANCE;
    }

    public void insertWord(String username, String word) {
        if (username == null || word == null || word.isBlank()) {
            return;
        }
        entries.add(new WordEntry(username, word.trim().toLowerCase()));
        trimToTop5();
    }

    public List<WordEntry> getTop5Words() {
        List<WordEntry> sorted = new ArrayList<>(entries);
        sorted.sort(Comparator.comparingInt((WordEntry e) -> e.word().length()).reversed()
                .thenComparing(WordEntry::username));
        if (sorted.size() <= MAX_ENTRIES) {
            return sorted;
        }
        return sorted.subList(0, MAX_ENTRIES);
    }

    private void trimToTop5() {
        List<WordEntry> top = getTop5Words();
        entries.clear();
        entries.addAll(top);
    }
}
