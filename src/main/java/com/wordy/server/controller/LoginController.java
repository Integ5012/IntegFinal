package com.wordy.server.controller;

import com.wordy.grpc.BasicResponse;
import com.wordy.grpc.LoginRequest;
import com.wordy.grpc.LoginResponse;
import com.wordy.grpc.LoginServiceGrpc;
import com.wordy.grpc.LogoutRequest;
import com.wordy.grpc.RegisterPlayerRequest;
import com.wordy.server.service.AuthService;
import com.wordy.server.service.dto.AuthResult;
import com.wordy.server.service.dto.OperationResult;
import com.wordy.server.view.GrpcViewMapper;
import io.grpc.stub.StreamObserver;

public class LoginController extends LoginServiceGrpc.LoginServiceImplBase {

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        try {
            AuthResult result = authService.login(request.getUsername(), request.getPassword());
            responseObserver.onNext(GrpcViewMapper.toLoginResponse(result));
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onNext(LoginResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Server error during login: " + e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void logout(LogoutRequest request, StreamObserver<BasicResponse> responseObserver) {
        try {
            OperationResult result = authService.logout(request);
            responseObserver.onNext(GrpcViewMapper.toBasicResponse(result));
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onNext(BasicResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Server error during logout: " + e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void registerPlayer(RegisterPlayerRequest request, StreamObserver<BasicResponse> responseObserver) {
        try {
            OperationResult result = authService.registerPlayer(request.getUsername());
            responseObserver.onNext(GrpcViewMapper.toBasicResponse(result));
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onNext(BasicResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Server error during registration: " + e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }
}
