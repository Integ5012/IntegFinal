package com.wordy.server.game;

import java.util.Map;

public record RoundOutcome(
        String winnerUsername,
        String bestWord,
        Map<String, String> playerWords
) {
    public boolean hasWinner() {
        return winnerUsername != null && !winnerUsername.isEmpty();
    }
}
