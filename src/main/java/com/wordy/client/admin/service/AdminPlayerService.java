package com.wordy.client.admin.service;

import com.wordy.client.common.GrpcConnectionFactory;
import com.wordy.common.ClientConfig;
import com.wordy.grpc.*;
import io.grpc.ManagedChannel;

public class AdminPlayerService implements AutoCloseable {

    private final ManagedChannel channel;
    private final AdminServiceGrpc.AdminServiceBlockingStub adminStub;
    private final LoginServiceGrpc.LoginServiceBlockingStub loginStub;
    private final String sessionId;

    public AdminPlayerService(ClientConfig.Settings settings, String sessionId) {
        channel = GrpcConnectionFactory.open(settings);
        adminStub = AdminServiceGrpc.newBlockingStub(channel);
        loginStub = LoginServiceGrpc.newBlockingStub(channel);
        this.sessionId = sessionId;
    }

    public void logout() {
        if (sessionId != null && !sessionId.isBlank()) {
            loginStub.logout(LogoutRequest.newBuilder().setSessionId(sessionId).build());
        }
    }

    public BasicResponse createPlayer(String username) {
        return adminStub.createPlayer(CreatePlayerRequest.newBuilder()
                .setUsername(username)
                .setPassword("")
                .build());
    }

    public BasicResponse updatePlayer(int id, String username, String password, int wins) {
        return adminStub.updatePlayer(UpdatePlayerRequest.newBuilder()
                .setId(id)
                .setUsername(username)
                .setPassword(password)
                .setWins(wins)
                .build());
    }

    public BasicResponse deletePlayer(int id) {
        return adminStub.deletePlayer(DeletePlayerRequest.newBuilder().setId(id).build());
    }

    public SearchPlayerResponse searchPlayers(String keyword) {
        return adminStub.searchPlayer(SearchPlayerRequest.newBuilder()
                .setKeyword(keyword)
                .build());
    }

    public BasicResponse updateConfig(int waitTime, int roundTime) {
        return adminStub.updateGameConfig(GameConfigRequest.newBuilder()
                .setWaitTime(waitTime)
                .setRoundTime(roundTime)
                .build());
    }

    public GameConfigRequest getGameConfig() {
        return adminStub.getGameConfig(Empty.newBuilder().build());
    }

    @Override
    public void close() {
        channel.shutdown();
    }
}
