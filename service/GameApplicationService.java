package com.wordy.server.service;

import com.wordy.grpc.GameEvent;
import com.wordy.grpc.SubmitWordRequest;
import com.wordy.grpc.SubmitWordResponse;
import com.wordy.server.game.GameLobby;
import com.wordy.server.game.GameSession;
import com.wordy.server.game.LetterGenerator;
import com.wordy.server.game.WordDictionary;
import com.wordy.server.store.InMemoryConfigStore;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.Optional;

public class GameApplicationService {

    private final SessionRegistry sessionRegistry;
    private final GameLobby gameLobby;

    public GameApplicationService() throws IOException {
        SessionRegistry registry = SessionRegistry.getInstance();
        this.sessionRegistry = registry;
        this.gameLobby = new GameLobby(
                WordDictionary.loadDefault(),
                new LetterGenerator(),
                InMemoryConfigStore.getInstance(),
                registry
        );
    }

    GameApplicationService(SessionRegistry sessionRegistry, GameLobby gameLobby) {
        this.sessionRegistry = sessionRegistry;
        this.gameLobby = gameLobby;
    }

    public void joinGame(String sessionId, StreamObserver<GameEvent> responseObserver) {
        if (!sessionRegistry.isCurrentSession(sessionId)) {
            responseObserver.onError(Status.UNAUTHENTICATED
                    .withDescription("Session expired or replaced by a new login")
                    .asRuntimeException());
            return;
        }

        Optional<String> username = sessionRegistry.resolveUsername(sessionId);
        if (username.isEmpty()) {
            responseObserver.onError(Status.UNAUTHENTICATED
                    .withDescription("Invalid session")
                    .asRuntimeException());
            return;
        }

        String status = gameLobby.join(username.get(), sessionId, responseObserver);
        if (status.startsWith("Session expired") || status.startsWith("Account already")) {
            responseObserver.onError(Status.FAILED_PRECONDITION
                    .withDescription(status)
                    .asRuntimeException());
        }
    }

    public void submitWord(SubmitWordRequest request, StreamObserver<SubmitWordResponse> responseObserver) {
        if (!sessionRegistry.isCurrentSession(request.getSessionId())) {
            responseObserver.onNext(SubmitWordResponse.newBuilder()
                    .setValid(false)
                    .setMessage("Session expired or replaced by a new login")
                    .build());
            responseObserver.onCompleted();
            return;
        }

        Optional<String> username = sessionRegistry.resolveUsername(request.getSessionId());
        if (username.isEmpty()) {
            responseObserver.onNext(SubmitWordResponse.newBuilder()
                    .setValid(false)
                    .setMessage("Invalid session")
                    .build());
            responseObserver.onCompleted();
            return;
        }

        GameSession session = gameLobby.getActiveSession();
        if (session == null || !session.hasPlayer(username.get())) {
            responseObserver.onNext(SubmitWordResponse.newBuilder()
                    .setValid(false)
                    .setMessage("No active game")
                    .build());
            responseObserver.onCompleted();
            return;
        }

        SubmitWordResponse response = session.submitWord(username.get(), request.getWord());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public GameLobby getGameLobby() {
        return gameLobby;
    }
}
