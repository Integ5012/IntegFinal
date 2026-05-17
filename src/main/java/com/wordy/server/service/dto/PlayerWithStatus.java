package com.wordy.server.service.dto;

import com.wordy.server.model.entity.Player;

public record PlayerWithStatus(Player player, boolean online, boolean inGame, boolean inQueue) {
}
