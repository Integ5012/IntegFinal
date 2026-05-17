package com.wordy.server.model.repository;

import com.wordy.server.model.entity.Player;

import java.util.List;

public interface PlayerRepository {

    Player findByUsername(String username);

    Player findById(int id);

    List<Player> findAllPlayers();

    List<Player> searchPlayers(String keyword);

    boolean createPlayer(String username, String password, String role);

    boolean updatePlayer(int id, String username, String password, int wins);

    boolean deletePlayer(int id);

    boolean incrementWins(String username);

    List<Player> findTopPlayersByWins(int limit);
}
