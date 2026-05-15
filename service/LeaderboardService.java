package com.wordy.server.service;

import com.wordy.server.model.entity.LongestWordEntry;
import com.wordy.server.model.entity.Player;
import com.wordy.server.model.repository.LeaderboardRepository;
import com.wordy.server.model.repository.PlayerRepository;

import java.util.List;

public class LeaderboardService {

    private static final int TOP_PLAYER_LIMIT = 5;

    private final PlayerRepository playerRepository;
    private final LeaderboardRepository leaderboardRepository;

    public LeaderboardService(PlayerRepository playerRepository, LeaderboardRepository leaderboardRepository) {
        this.playerRepository = playerRepository;
        this.leaderboardRepository = leaderboardRepository;
    }

    public List<Player> getTopPlayers() {
        return playerRepository.findTopPlayersByWins(TOP_PLAYER_LIMIT);
    }

    public List<LongestWordEntry> getLongestWords() {
        return leaderboardRepository.getTopWords(TOP_PLAYER_LIMIT);
    }
}
