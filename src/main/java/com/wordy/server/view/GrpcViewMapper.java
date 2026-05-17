package com.wordy.server.view;

import com.wordy.grpc.BasicResponse;
import com.wordy.grpc.GameConfigRequest;
import com.wordy.grpc.LeaderboardResponse;
import com.wordy.grpc.LoginResponse;
import com.wordy.grpc.LongestWordRecord;
import com.wordy.grpc.SearchPlayerResponse;
import com.wordy.server.model.entity.LongestWordEntry;
import com.wordy.server.model.entity.TimeConfig;
import com.wordy.server.service.dto.AuthResult;
import com.wordy.server.service.dto.OperationResult;
import com.wordy.server.service.dto.PlayerWithStatus;

import java.util.List;

public final class GrpcViewMapper {

    private GrpcViewMapper() {
    }

    public static LoginResponse toLoginResponse(AuthResult result) {
        LoginResponse.Builder builder = LoginResponse.newBuilder()
                .setSuccess(result.success())
                .setMessage(result.message());
        if (result.role() != null) {
            builder.setRole(result.role());
        }
        if (result.sessionId() != null) {
            builder.setSessionId(result.sessionId());
        }
        return builder.build();
    }

    public static BasicResponse toBasicResponse(OperationResult result) {
        return BasicResponse.newBuilder()
                .setSuccess(result.success())
                .setMessage(result.message())
                .build();
    }

    public static com.wordy.grpc.Player toProtoPlayer(com.wordy.server.model.entity.Player player) {
        return toProtoPlayer(player, false, false, false);
    }

    public static com.wordy.grpc.Player toProtoPlayer(
            com.wordy.server.model.entity.Player player,
            boolean online,
            boolean inGame,
            boolean inQueue) {
        return com.wordy.grpc.Player.newBuilder()
                .setId(player.id())
                .setUsername(player.username())
                .setWins(player.wins())
                .setOnline(online)
                .setInGame(inGame)
                .setInQueue(inQueue)
                .build();
    }

    public static SearchPlayerResponse toSearchPlayerResponse(List<com.wordy.server.model.entity.Player> players) {
        SearchPlayerResponse.Builder builder = SearchPlayerResponse.newBuilder();
        for (com.wordy.server.model.entity.Player player : players) {
            builder.addPlayers(toProtoPlayer(player));
        }
        return builder.build();
    }

    public static SearchPlayerResponse toSearchPlayerResponseWithStatus(List<PlayerWithStatus> players) {
        SearchPlayerResponse.Builder builder = SearchPlayerResponse.newBuilder();
        for (PlayerWithStatus row : players) {
            builder.addPlayers(toProtoPlayer(
                    row.player(), row.online(), row.inGame(), row.inQueue()));
        }
        return builder.build();
    }

    public static GameConfigRequest toGameConfigRequest(TimeConfig config) {
        return GameConfigRequest.newBuilder()
                .setWaitTime(config.getWaitingTime())
                .setRoundTime(config.getRoundDuration())
                .build();
    }

    public static LeaderboardResponse toLeaderboardResponse(
            List<com.wordy.server.model.entity.Player> topPlayers,
            List<LongestWordEntry> longestWords
    ) {
        LeaderboardResponse.Builder builder = LeaderboardResponse.newBuilder();
        for (com.wordy.server.model.entity.Player player : topPlayers) {
            builder.addTopPlayers(toProtoPlayer(player));
        }
        for (LongestWordEntry entry : longestWords) {
            builder.addLongestWords(LongestWordRecord.newBuilder()
                    .setUsername(entry.username())
                    .setWord(entry.word())
                    .build());
        }
        return builder.build();
    }
}
