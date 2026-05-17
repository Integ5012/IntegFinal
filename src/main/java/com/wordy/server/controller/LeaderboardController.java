package com.wordy.server.controller;

import com.wordy.grpc.Empty;
import com.wordy.grpc.LeaderboardResponse;
import com.wordy.grpc.LeaderboardServiceGrpc;
import com.wordy.server.service.LeaderboardService;
import com.wordy.server.view.GrpcViewMapper;
import io.grpc.stub.StreamObserver;

public class LeaderboardController extends LeaderboardServiceGrpc.LeaderboardServiceImplBase {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @Override
    public void getTopPlayers(Empty request, StreamObserver<LeaderboardResponse> responseObserver) {
        LeaderboardResponse response = GrpcViewMapper.toLeaderboardResponse(
                leaderboardService.getTopPlayers(),
                leaderboardService.getLongestWords()
        );
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
