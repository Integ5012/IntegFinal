package com.wordy.client.admin.model;

import com.wordy.grpc.AdminServiceGrpc;
import com.wordy.grpc.BasicResponse;
import com.wordy.grpc.CreatePlayerRequest;
import com.wordy.grpc.DeletePlayerRequest;
import com.wordy.grpc.Empty;
import com.wordy.grpc.GameConfigRequest;
import com.wordy.grpc.LoginRequest;
import com.wordy.grpc.LoginResponse;
import com.wordy.grpc.LoginServiceGrpc;
import com.wordy.grpc.SearchPlayerRequest;
import com.wordy.grpc.SearchPlayerResponse;
import com.wordy.grpc.UpdatePlayerRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class AdminModel {

    private final ManagedChannel channel;
    private final LoginServiceGrpc.LoginServiceBlockingStub loginStub;
    private final AdminServiceGrpc.AdminServiceBlockingStub adminStub;
    private String sessionId;

    public AdminModel() {
        channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        loginStub = LoginServiceGrpc.newBlockingStub(channel);
        adminStub = AdminServiceGrpc.newBlockingStub(channel);
    }

    public LoginResponse login(String username, String password) {
        LoginResponse response = loginStub.login(LoginRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build());
        if (response.getSuccess()) {
            this.sessionId = response.getSessionId();
        }
        return response;
    }

    public BasicResponse createPlayer(String username, String password) {
        return adminStub.createPlayer(CreatePlayerRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
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

    public BasicResponse updateConfig(int waitTime, int roundTime) {
        return adminStub.updateGameConfig(GameConfigRequest.newBuilder()
                .setWaitTime(waitTime)
                .setRoundTime(roundTime)
                .build());
    }

    public GameConfigRequest getGameConfig() {
        return adminStub.getGameConfig(Empty.newBuilder().build());
    }

    public BasicResponse deletePlayer(int id) {
        return adminStub.deletePlayer(DeletePlayerRequest.newBuilder()
                .setId(id)
                .build());
    }

    public SearchPlayerResponse searchPlayer(String keyword) {
        return adminStub.searchPlayer(SearchPlayerRequest.newBuilder()
                .setKeyword(keyword)
                .build());
    }

    public String getSessionId() {
        return sessionId;
    }

    public void shutdown() {
        channel.shutdown();
    }
}
