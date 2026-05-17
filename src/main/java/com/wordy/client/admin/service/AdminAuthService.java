package com.wordy.client.admin.service;

import com.wordy.client.common.GrpcConnectionFactory;
import com.wordy.common.ClientConfig;
import com.wordy.grpc.LoginRequest;
import com.wordy.grpc.LoginResponse;
import com.wordy.grpc.LoginServiceGrpc;
import io.grpc.ManagedChannel;

public class AdminAuthService implements AutoCloseable {

    private final ManagedChannel channel;
    private final LoginServiceGrpc.LoginServiceBlockingStub loginStub;
    private String sessionId;

    public AdminAuthService(ClientConfig.Settings settings) {
        channel = GrpcConnectionFactory.open(settings);
        loginStub = LoginServiceGrpc.newBlockingStub(channel);
    }

    public LoginResponse login(String username, String password) {
        LoginResponse response = loginStub.login(LoginRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build());
        if (response.getSuccess()) {
            sessionId = response.getSessionId();
        }
        return response;
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public void close() {
        channel.shutdown();
    }
}
