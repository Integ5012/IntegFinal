package com.wordy.server.service;

import com.wordy.grpc.BasicResponse;
import com.wordy.grpc.CreatePlayerRequest;
import com.wordy.grpc.DeletePlayerRequest;
import com.wordy.grpc.GameConfigRequest;
import com.wordy.grpc.Player;
import com.wordy.grpc.SearchPlayerRequest;
import com.wordy.grpc.SearchPlayerResponse;
import com.wordy.grpc.UpdatePlayerRequest;
import com.wordy.server.store.InMemoryConfigStore;
import com.wordy.server.store.InMemoryPlayerStore;

import java.util.List;

public class AdminApplicationService {

    private static final String PLAYER_ROLE = "PLAYER";

    private final InMemoryPlayerStore playerStore;
    private final InMemoryConfigStore configStore;

    public AdminApplicationService() {
        this(InMemoryPlayerStore.getInstance(), InMemoryConfigStore.getInstance());
    }

    public AdminApplicationService(InMemoryPlayerStore playerStore, InMemoryConfigStore configStore) {
        this.playerStore = playerStore;
        this.configStore = configStore;
    }

    public BasicResponse createPlayer(CreatePlayerRequest request) {
        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        String password = request.getPassword() == null ? "" : request.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            return failure("Username and password are required");
        }

        if (playerStore.getPlayerByUsername(username) != null) {
            return failure("Username already exists");
        }

        boolean created = playerStore.createPlayer(username, password, PLAYER_ROLE);
        return created
                ? success("Player created successfully")
                : failure("Failed to create player");
    }

    public BasicResponse updatePlayer(UpdatePlayerRequest request) {
        if (request.getId() <= 0) {
            return failure("Invalid player id");
        }

        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        String password = request.getPassword() == null ? "" : request.getPassword();

        if (username.isEmpty()) {
            return failure("Username is required");
        }

        if (request.getWins() < 0) {
            return failure("Wins cannot be negative");
        }

        InMemoryPlayerStore.PlayerRecord existing = playerStore.getPlayerById(request.getId());
        if (existing == null || !PLAYER_ROLE.equals(existing.role())) {
            return failure("Player not found");
        }

        boolean updated = playerStore.updatePlayer(
                request.getId(),
                username,
                password,
                request.getWins()
        );
        return updated
                ? success("Player updated successfully")
                : failure("Failed to update player");
    }

    public BasicResponse deletePlayer(DeletePlayerRequest request) {
        if (request.getId() <= 0) {
            return failure("Invalid player id");
        }

        InMemoryPlayerStore.PlayerRecord existing = playerStore.getPlayerById(request.getId());
        if (existing == null || !PLAYER_ROLE.equals(existing.role())) {
            return failure("Player not found");
        }

        boolean deleted = playerStore.deletePlayer(request.getId());
        return deleted
                ? success("Player deleted successfully")
                : failure("Failed to delete player");
    }

    public SearchPlayerResponse searchPlayers(SearchPlayerRequest request) {
        String keyword = request.getKeyword() == null ? "" : request.getKeyword().trim();

        List<InMemoryPlayerStore.PlayerRecord> records = keyword.isEmpty()
                ? playerStore.getAllPlayers()
                : playerStore.searchPlayers(keyword);

        SearchPlayerResponse.Builder builder = SearchPlayerResponse.newBuilder();
        for (InMemoryPlayerStore.PlayerRecord record : records) {
            builder.addPlayers(toProtoPlayer(record));
        }
        return builder.build();
    }

    public BasicResponse updateGameConfig(GameConfigRequest request) {
        if (request.getWaitTime() <= 0 || request.getRoundTime() <= 0) {
            return failure("Wait time and round time must be greater than zero");
        }

        boolean updated = configStore.setConfig(request.getWaitTime(), request.getRoundTime());
        return updated
                ? success("Game configuration updated")
                : failure("Failed to update game configuration");
    }

    public GameConfigRequest getGameConfig() {
        var config = configStore.getConfig();
        return GameConfigRequest.newBuilder()
                .setWaitTime(config.getWaitingTime())
                .setRoundTime(config.getRoundDuration())
                .build();
    }

    private static Player toProtoPlayer(InMemoryPlayerStore.PlayerRecord record) {
        return Player.newBuilder()
                .setId(record.id())
                .setUsername(record.username())
                .setWins(record.wins())
                .build();
    }

    private static BasicResponse success(String message) {
        return BasicResponse.newBuilder()
                .setSuccess(true)
                .setMessage(message)
                .build();
    }

    private static BasicResponse failure(String message) {
        return BasicResponse.newBuilder()
                .setSuccess(false)
                .setMessage(message)
                .build();
    }
}
