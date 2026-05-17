package com.wordy.server.controller;

import com.wordy.grpc.GameEvent;
import com.wordy.grpc.GameServiceGrpc;
import com.wordy.grpc.JoinGameRequest;
import com.wordy.grpc.SubmitWordRequest;
import com.wordy.grpc.SubmitWordResponse;
import com.wordy.server.service.GameService;
import io.grpc.stub.StreamObserver;

public class GameController extends GameServiceGrpc.GameServiceImplBase {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void joinGame(JoinGameRequest request, StreamObserver<GameEvent> responseObserver) {
        gameService.joinGame(request.getSessionId(), responseObserver);
    }

    @Override
    public void submitWord(SubmitWordRequest request, StreamObserver<SubmitWordResponse> responseObserver) {
        gameService.submitWord(request, responseObserver);
    }
}
