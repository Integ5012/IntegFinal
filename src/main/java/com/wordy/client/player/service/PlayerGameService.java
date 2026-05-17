package com.wordy.client.player.service;

import com.wordy.client.common.GrpcConnectionFactory;
import com.wordy.client.player.model.PlayerSession;
import com.wordy.common.ClientConfig;
import com.wordy.grpc.*;
import io.grpc.ManagedChannel;

import java.util.Iterator;

/**
 * MVC service layer: in-game actions over gRPC.
 */
public class PlayerGameService implements AutoCloseable {

    private final ManagedChannel channel;
    private final GameServiceGrpc.GameServiceBlockingStub gameStub;
    private final LeaderboardServiceGrpc.LeaderboardServiceBlockingStub leaderboardStub;
    private final PlayerSession session;

    public PlayerGameService(ClientConfig.Settings settings, PlayerSession session) {
        this.channel = GrpcConnectionFactory.open(settings);
        this.gameStub = GameServiceGrpc.newBlockingStub(channel);
        this.leaderboardStub = LeaderboardServiceGrpc.newBlockingStub(channel);
        this.session = session;
    }

    public BasicResponse logout() {
        if (!session.isLoggedIn()) {
            return BasicResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Already logged out")
                    .build();
        }
        BasicResponse response = LoginServiceGrpc.newBlockingStub(channel).logout(
                LogoutRequest.newBuilder().setSessionId(session.getSessionId()).build());
        session.clear();
        return response;
    }

    public Iterator<GameEvent> joinGame() {
        return gameStub.joinGame(JoinGameRequest.newBuilder()
                .setSessionId(session.getSessionId())
                .build());
    }

    public SubmitWordResponse submitWord(String word) {
        return gameStub.submitWord(SubmitWordRequest.newBuilder()
                .setSessionId(session.getSessionId())
                .setWord(word)
                .build());
    }

    public LeaderboardResponse getLeaderboard() {
        return leaderboardStub.getTopPlayers(Empty.newBuilder().build());
    }

    @Override
    public void close() {
        channel.shutdown();
    }
}
