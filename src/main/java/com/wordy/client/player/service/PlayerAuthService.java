package com.wordy.client.player.service;

import com.wordy.client.common.GrpcConnectionFactory;
import com.wordy.common.ClientConfig;
import com.wordy.grpc.BasicResponse;
import com.wordy.grpc.LoginResponse;
import com.wordy.grpc.LoginServiceGrpc;
import com.wordy.grpc.RegisterPlayerRequest;
import io.grpc.ManagedChannel;

/**
 * MVC service layer: player authentication and registration over gRPC.
 */
public class PlayerAuthService implements AutoCloseable {

    private final ManagedChannel channel;
    private final LoginServiceGrpc.LoginServiceBlockingStub loginStub;

    public PlayerAuthService(ClientConfig.Settings settings) {
        this.channel = GrpcConnectionFactory.open(settings);
        this.loginStub = LoginServiceGrpc.newBlockingStub(channel);
    }

    public LoginResponse login(String username, String password) {
        return loginStub.login(com.wordy.grpc.LoginRequest.newBuilder()
                .setUsername(username)
                .setPassword(password == null ? "" : password)
                .build());
    }

    public BasicResponse register(String username) {
        return loginStub.registerPlayer(RegisterPlayerRequest.newBuilder()
                .setUsername(username)
                .build());
    }

    @Override
    public void close() {
        channel.shutdown();
    }
}
