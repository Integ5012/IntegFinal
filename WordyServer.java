package com.wordy.server;

import com.wordy.server.grpc.AdminGrpcService;
import com.wordy.server.grpc.GameGrpcService;
import com.wordy.server.grpc.LeaderboardGrpcService;
import com.wordy.server.grpc.LoginGrpcService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class WordyServer {

    public static final int DEFAULT_PORT = 9090;

    private final int port;
    private Server server;

    public WordyServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new LoginGrpcService())
                .addService(new AdminGrpcService())
                .addService(new GameGrpcService())
                .addService(new LeaderboardGrpcService())
                .build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                WordyServer.this.stop();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));
    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        WordyServer wordyServer = new WordyServer(port);
        wordyServer.start();
        System.out.println("Wordy server started on port " + port);
        wordyServer.blockUntilShutdown();
    }
}
