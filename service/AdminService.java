package com.wordy.server.service;

import com.wordy.grpc.CreatePlayerRequest;
import com.wordy.grpc.DeletePlayerRequest;
import com.wordy.grpc.GameConfigRequest;
import com.wordy.grpc.SearchPlayerRequest;
import com.wordy.grpc.UpdatePlayerRequest;
import com.wordy.server.model.entity.Player;
import com.wordy.server.model.entity.TimeConfig;
import com.wordy.server.model.game.GameLobby;
import com.wordy.server.model.repository.GameConfigRepository;
import com.wordy.server.model.repository.PlayerRepository;
import com.wordy.server.model.session.SessionRegistry;
import com.wordy.server.service.dto.OperationResult;
import com.wordy.server.service.dto.PlayerWithStatus;

import java.util.ArrayList;
import java.util.List;

public class AdminService {

    private static final String PLAYER_ROLE = "PLAYER";

    private final PlayerRepository playerRepository;
    private final GameConfigRepository configRepository;
    private final SessionRegistry sessionRegistry;
    private final GameLobby gameLobby;

    public AdminService(
            PlayerRepository playerRepository,
            GameConfigRepository configRepository,
            SessionRegistry sessionRegistry,
            GameLobby gameLobby) {
        this.playerRepository = playerRepository;
        this.configRepository = configRepository;
        this.sessionRegistry = sessionRegistry;
        this.gameLobby = gameLobby;
    }

    public OperationResult createPlayer(CreatePlayerRequest request) {
        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        String password = request.getPassword() == null ? "" : request.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            return OperationResult.failure("Username and password are required");
        }

        if (playerRepository.findByUsername(username) != null) {
            return OperationResult.failure("Username already exists");
        }

        return playerRepository.createPlayer(username, password, PLAYER_ROLE)
                ? OperationResult.success("Player created successfully")
                : OperationResult.failure("Failed to create player");
    }

    public OperationResult updatePlayer(UpdatePlayerRequest request) {
        if (request.getId() <= 0) {
            return OperationResult.failure("Invalid player id");
        }

        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        String password = request.getPassword() == null ? "" : request.getPassword();

        if (username.isEmpty()) {
            return OperationResult.failure("Username is required");
        }

        if (request.getWins() < 0) {
            return OperationResult.failure("Wins cannot be negative");
        }

        Player existing = playerRepository.findById(request.getId());
        if (existing == null || !PLAYER_ROLE.equals(existing.role())) {
            return OperationResult.failure("Player not found");
        }

        return playerRepository.updatePlayer(request.getId(), username, password, request.getWins())
                ? OperationResult.success("Player updated successfully")
                : OperationResult.failure("Failed to update player");
    }

    public OperationResult deletePlayer(DeletePlayerRequest request) {
        if (request.getId() <= 0) {
            return OperationResult.failure("Invalid player id");
        }

        Player existing = playerRepository.findById(request.getId());
        if (existing == null || !PLAYER_ROLE.equals(existing.role())) {
            return OperationResult.failure("Player not found");
        }

        return playerRepository.deletePlayer(request.getId())
                ? OperationResult.success("Player deleted successfully")
                : OperationResult.failure("Failed to delete player");
    }

    public List<Player> searchPlayers(SearchPlayerRequest request) {
        String keyword = request.getKeyword() == null ? "" : request.getKeyword().trim();
        return keyword.isEmpty()
                ? playerRepository.findAllPlayers()
                : playerRepository.searchPlayers(keyword);
    }

    public List<PlayerWithStatus> searchPlayersWithStatus(SearchPlayerRequest request) {
        List<PlayerWithStatus> result = new ArrayList<>();
        for (Player player : searchPlayers(request)) {
            if (!PLAYER_ROLE.equals(player.role())) {
                continue;
            }
            String name = player.username();
            boolean online = sessionRegistry.isOnline(name);
            boolean inGame = sessionRegistry.isInActiveGame(name);
            boolean inQueue = gameLobby.isInQueue(name);
            result.add(new PlayerWithStatus(player, online, inGame, inQueue));
        }
        return result;
    }

    public OperationResult updateGameConfig(GameConfigRequest request) {
        if (request.getWaitTime() <= 0 || request.getRoundTime() <= 0) {
            return OperationResult.failure("Wait time and round time must be greater than zero");
        }

        return configRepository.setConfig(request.getWaitTime(), request.getRoundTime())
                ? OperationResult.success("Game configuration updated")
                : OperationResult.failure("Failed to update game configuration");
    }

    public TimeConfig getGameConfig() {
        return configRepository.getConfig();
    }
}
