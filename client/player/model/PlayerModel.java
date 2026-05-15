package com.wordy.client.player.model;

import com.wordy.common.EndpointConfig;
import com.wordy.grpc.*;
import io.grpc.ManagedChannel;
import java.util.Iterator;
import io.grpc.ManagedChannelBuilder;

public class PlayerModel {

    private final ManagedChannel channel;

    // gRPC blocking stubs
    private final LoginServiceGrpc.LoginServiceBlockingStub loginStub;
    private final GameServiceGrpc.GameServiceBlockingStub gameStub;
    private final LeaderboardServiceGrpc.LeaderboardServiceBlockingStub leaderboardStub;

    private String sessionId;
    private String username;

    public PlayerModel() {
        channel = ManagedChannelBuilder.forAddress(EndpointConfig.host(), EndpointConfig.clientPort())
                .usePlaintext()
                .build();

        loginStub = LoginServiceGrpc.newBlockingStub(channel);
        gameStub = GameServiceGrpc.newBlockingStub(channel);
        leaderboardStub = LeaderboardServiceGrpc.newBlockingStub(channel);
    }
    public LoginResponse login(String username, String password) {
        LoginRequest request = LoginRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();

        LoginResponse response = loginStub.login(request);

        if (response.getSuccess()) {
            this.sessionId = response.getSessionId();
            this.username = username;
        }

        return response;
    }

    public BasicResponse logout() {
        LogoutRequest request = LogoutRequest.newBuilder()
                .setSessionId(sessionId)
                .build();

        return loginStub.logout(request);
    }

    public Iterator<GameEvent> joinGame() {
        JoinGameRequest request = JoinGameRequest.newBuilder()
                .setSessionId(sessionId)
                .build();

        return gameStub.joinGame(request);
    }

    public SubmitWordResponse submitWord(String word) {
        SubmitWordRequest request = SubmitWordRequest.newBuilder()
                .setSessionId(sessionId)
                .setWord(word)
                .build();

        return gameStub.submitWord(request);
    }
    public LeaderboardResponse getLeaderboard() {
        return leaderboardStub.getTopPlayers(Empty.newBuilder().build());
    }

    public String getUsername() {
        return username;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void shutdown() {
        channel.shutdown();
    }
}