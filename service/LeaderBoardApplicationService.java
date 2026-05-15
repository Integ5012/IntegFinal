package com.wordy.server.service;

import com.wordy.grpc.LeaderboardResponse;
import com.wordy.grpc.LongestWordRecord;
import com.wordy.grpc.Player;
import com.wordy.server.store.InMemoryLeaderboardStore;
import com.wordy.server.store.InMemoryPlayerStore;

import java.util.List;

public class LeaderBoardApplicationService {

    private static final int TOP_PLAYER_LIMIT = 5;

    private final InMemoryPlayerStore playerStore;
    private final InMemoryLeaderboardStore leaderboardStore;

    public LeaderBoardApplicationService() {
        this(InMemoryPlayerStore.getInstance(), InMemoryLeaderboardStore.getInstance());
    }

    public LeaderBoardApplicationService(
            InMemoryPlayerStore playerStore,
            InMemoryLeaderboardStore leaderboardStore
    ) {
        this.playerStore = playerStore;
        this.leaderboardStore = leaderboardStore;
    }

    public LeaderboardResponse getTopPlayers() {
        LeaderboardResponse.Builder builder = LeaderboardResponse.newBuilder();

        List<InMemoryPlayerStore.PlayerRecord> topPlayers =
                playerStore.getTopPlayersByWins(TOP_PLAYER_LIMIT);
        for (InMemoryPlayerStore.PlayerRecord record : topPlayers) {
            builder.addTopPlayers(Player.newBuilder()
                    .setId(record.id())
                    .setUsername(record.username())
                    .setWins(record.wins())
                    .build());
        }

        List<InMemoryLeaderboardStore.WordEntry> longestWords = leaderboardStore.getTop5Words();
        for (InMemoryLeaderboardStore.WordEntry entry : longestWords) {
            builder.addLongestWords(LongestWordRecord.newBuilder()
                    .setUsername(entry.username())
                    .setWord(entry.word())
                    .build());
        }

        return builder.build();
    }
}
