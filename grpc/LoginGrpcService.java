package com.wordy.server.grpc;

import com.wordy.grpc.BasicResponse;
import com.wordy.grpc.LoginRequest;
import com.wordy.grpc.LoginResponse;
import com.wordy.grpc.LoginServiceGrpc;
import com.wordy.grpc.LogoutRequest;
import com.wordy.server.service.AuthApplicationService;
import io.grpc.stub.StreamObserver;

public class LoginGrpcService extends LoginServiceGrpc.LoginServiceImplBase {

    private final AuthApplicationService authApplicationService;

    public LoginGrpcService() {
        this(new AuthApplicationService());
    }

    public LoginGrpcService(AuthApplicationService authApplicationService) {
        this.authApplicationService = authApplicationService;
    }

    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        try {
            LoginResponse response = authApplicationService.login(request);
            responseObserver.onNext(response);
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
            BasicResponse response = authApplicationService.logout(request);
            responseObserver.onNext(response);
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
}
