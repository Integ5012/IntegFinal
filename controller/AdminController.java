package com.wordy.server.controller;

import com.wordy.grpc.AdminServiceGrpc;
import com.wordy.grpc.BasicResponse;
import com.wordy.grpc.CreatePlayerRequest;
import com.wordy.grpc.DeletePlayerRequest;
import com.wordy.grpc.Empty;
import com.wordy.grpc.GameConfigRequest;
import com.wordy.grpc.SearchPlayerRequest;
import com.wordy.grpc.SearchPlayerResponse;
import com.wordy.grpc.UpdatePlayerRequest;
import com.wordy.server.service.AdminService;
import com.wordy.server.view.GrpcViewMapper;
import io.grpc.stub.StreamObserver;

public class AdminController extends AdminServiceGrpc.AdminServiceImplBase {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public void createPlayer(CreatePlayerRequest request, StreamObserver<BasicResponse> responseObserver) {
        respond(responseObserver, adminService.createPlayer(request));
    }

    @Override
    public void updatePlayer(UpdatePlayerRequest request, StreamObserver<BasicResponse> responseObserver) {
        respond(responseObserver, adminService.updatePlayer(request));
    }

    @Override
    public void deletePlayer(DeletePlayerRequest request, StreamObserver<BasicResponse> responseObserver) {
        respond(responseObserver, adminService.deletePlayer(request));
    }

    @Override
    public void searchPlayer(SearchPlayerRequest request, StreamObserver<SearchPlayerResponse> responseObserver) {
        responseObserver.onNext(GrpcViewMapper.toSearchPlayerResponse(adminService.searchPlayers(request)));
        responseObserver.onCompleted();
    }

    @Override
    public void updateGameConfig(GameConfigRequest request, StreamObserver<BasicResponse> responseObserver) {
        respond(responseObserver, adminService.updateGameConfig(request));
    }

    @Override
    public void getGameConfig(Empty request, StreamObserver<GameConfigRequest> responseObserver) {
        responseObserver.onNext(GrpcViewMapper.toGameConfigRequest(adminService.getGameConfig()));
        responseObserver.onCompleted();
    }

    private static void respond(StreamObserver<BasicResponse> responseObserver,
                                com.wordy.server.service.dto.OperationResult result) {
        responseObserver.onNext(GrpcViewMapper.toBasicResponse(result));
        responseObserver.onCompleted();
    }
}
