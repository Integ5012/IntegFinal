package com.wordy.server.grpc;

import com.wordy.grpc.Empty;
import com.wordy.grpc.LeaderboardResponse;
import com.wordy.grpc.LeaderboardServiceGrpc;
import com.wordy.server.service.LeaderBoardApplicationService;
import io.grpc.stub.StreamObserver;

public class LeaderboardGrpcService extends LeaderboardServiceGrpc.LeaderboardServiceImplBase {

    private final LeaderBoardApplicationService leaderBoardApplicationService;

    public LeaderboardGrpcService() {
        this(new LeaderBoardApplicationService());
    }

    public LeaderboardGrpcService(LeaderBoardApplicationService leaderBoardApplicationService) {
        this.leaderBoardApplicationService = leaderBoardApplicationService;
    }

    @Override
    public void getTopPlayers(Empty request, StreamObserver<LeaderboardResponse> responseObserver) {
        LeaderboardResponse response = leaderBoardApplicationService.getTopPlayers();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
