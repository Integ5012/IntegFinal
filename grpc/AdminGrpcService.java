package com.wordy.server.grpc;

import com.wordy.grpc.AdminServiceGrpc;
import com.wordy.grpc.BasicResponse;
import com.wordy.grpc.CreatePlayerRequest;
import com.wordy.grpc.DeletePlayerRequest;
import com.wordy.grpc.Empty;
import com.wordy.grpc.GameConfigRequest;
import com.wordy.grpc.SearchPlayerRequest;
import com.wordy.grpc.SearchPlayerResponse;
import com.wordy.grpc.UpdatePlayerRequest;
import com.wordy.server.service.AdminApplicationService;
import io.grpc.stub.StreamObserver;

public class AdminGrpcService extends AdminServiceGrpc.AdminServiceImplBase {

    private final AdminApplicationService adminApplicationService;

    public AdminGrpcService() {
        this(new AdminApplicationService());
    }

    public AdminGrpcService(AdminApplicationService adminApplicationService) {
        this.adminApplicationService = adminApplicationService;
    }

    @Override
    public void createPlayer(CreatePlayerRequest request, StreamObserver<BasicResponse> responseObserver) {
        respond(responseObserver, adminApplicationService.createPlayer(request));
    }

    @Override
    public void updatePlayer(UpdatePlayerRequest request, StreamObserver<BasicResponse> responseObserver) {
        respond(responseObserver, adminApplicationService.updatePlayer(request));
    }

    @Override
    public void deletePlayer(DeletePlayerRequest request, StreamObserver<BasicResponse> responseObserver) {
        respond(responseObserver, adminApplicationService.deletePlayer(request));
    }

    @Override
    public void searchPlayer(SearchPlayerRequest request, StreamObserver<SearchPlayerResponse> responseObserver) {
        SearchPlayerResponse response = adminApplicationService.searchPlayers(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateGameConfig(GameConfigRequest request, StreamObserver<BasicResponse> responseObserver) {
        respond(responseObserver, adminApplicationService.updateGameConfig(request));
    }

    @Override
    public void getGameConfig(Empty request, StreamObserver<GameConfigRequest> responseObserver) {
        GameConfigRequest response = adminApplicationService.getGameConfig();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private static void respond(StreamObserver<BasicResponse> responseObserver, BasicResponse response) {
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
