package com.wordy.client.player.model;

import com.wordy.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Iterator;

public class PlayerModel {

    private final ManagedChannel channel;
    private final LoginServiceGrpc.LoginServiceBlockingStub loginStub;
    private final GameServiceGrpc.GameServiceBlockingStub gameStub;
    private final LeaderboardServiceGrpc.LeaderboardServiceBlockingStub leaderboardStub;

    private String sessionId;
    private String username;

    public PlayerModel(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        loginStub = LoginServiceGrpc.newBlockingStub(channel);
        gameStub = GameServiceGrpc.newBlockingStub(channel);
        leaderboardStub = LeaderboardServiceGrpc.newBlockingStub(channel);
    }

    public LoginResponse login(String username, String password) {
        LoginRequest request = LoginRequest.newBuilder()
                .setUsername(username)
                .setPassword(password == null ? "" : password)
                .build();

        LoginResponse response = loginStub.login(request);

        if (response.getSuccess()) {
            this.sessionId = response.getSessionId();
            this.username = username;
        }

        return response;
    }

    public BasicResponse register(String username) {
        return loginStub.registerPlayer(RegisterPlayerRequest.newBuilder()
                .setUsername(username)
                .build());
    }

    public BasicResponse logout() {
        if (sessionId == null || sessionId.isBlank()) {
            return BasicResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Already logged out")
                    .build();
        }
        LogoutRequest request = LogoutRequest.newBuilder()
                .setSessionId(sessionId)
                .build();
        BasicResponse response = loginStub.logout(request);
        sessionId = null;
        username = null;
        return response;
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
