package com.wordy.server.grpc;

import com.wordy.grpc.GameServiceGrpc;
import com.wordy.grpc.JoinGameRequest;
import com.wordy.grpc.SubmitWordRequest;
import com.wordy.grpc.SubmitWordResponse;
import com.wordy.grpc.GameEvent;
import com.wordy.server.service.GameApplicationService;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.io.UncheckedIOException;

public class GameGrpcService extends GameServiceGrpc.GameServiceImplBase {

    private final GameApplicationService gameApplicationService;

    public GameGrpcService() {
        try {
            this.gameApplicationService = new GameApplicationService();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load words.txt", e);
        }
    }

    public GameGrpcService(GameApplicationService gameApplicationService) {
        this.gameApplicationService = gameApplicationService;
    }

    @Override
    public void joinGame(JoinGameRequest request, StreamObserver<GameEvent> responseObserver) {
        gameApplicationService.joinGame(request.getSessionId(), responseObserver);
    }

    @Override
    public void submitWord(SubmitWordRequest request, StreamObserver<SubmitWordResponse> responseObserver) {
        gameApplicationService.submitWord(request, responseObserver);
    }
}
