package com.wordy.server.model.repository.memory;

import com.wordy.server.model.entity.Player;
import com.wordy.server.model.repository.PlayerRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryPlayerRepository implements PlayerRepository {

    private static final InMemoryPlayerRepository INSTANCE = new InMemoryPlayerRepository();

    private final Map<Integer, Player> byId = new ConcurrentHashMap<>();
    private final Map<String, Player> byUsername = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public static InMemoryPlayerRepository getInstance() {
        return INSTANCE;
    }

    private InMemoryPlayerRepository() {
        seed("admin", "1234", "ADMIN", 0);
        seed("player1", "1234", "PLAYER", 0);
        seed("player2", "1234", "PLAYER", 0);
    }

    private void seed(String username, String password, String role, int wins) {
        int id = nextId.getAndIncrement();
        Player player = new Player(id, username, password, role, wins);
        byId.put(id, player);
        byUsername.put(normalize(username), player);
    }

    @Override
    public Player findByUsername(String username) {
        if (username == null) {
            return null;
        }
        return byUsername.get(normalize(username));
    }

    @Override
    public Player findById(int id) {
        return byId.get(id);
    }

    @Override
    public List<Player> findAllPlayers() {
        return byId.values().stream()
                .filter(p -> "PLAYER".equals(p.role()))
                .sorted(Comparator.comparing(Player::username))
                .collect(Collectors.toList());
    }

    @Override
    public List<Player> searchPlayers(String keyword) {
        String lower = keyword.toLowerCase(Locale.ROOT);
        return findAllPlayers().stream()
                .filter(p -> p.username().toLowerCase(Locale.ROOT).contains(lower))
                .collect(Collectors.toList());
    }

    @Override
    public boolean createPlayer(String username, String password, String role) {
        if (username == null || byUsername.containsKey(normalize(username))) {
            return false;
        }
        int id = nextId.getAndIncrement();
        Player player = new Player(id, username.trim(), password, role, 0);
        byId.put(id, player);
        byUsername.put(normalize(username), player);
        return true;
    }

    @Override
    public boolean updatePlayer(int id, String username, String password, int wins) {
        Player existing = byId.get(id);
        if (existing == null || !"PLAYER".equals(existing.role())) {
            return false;
        }

        byUsername.remove(normalize(existing.username()));

        String newUsername = username.trim();
        String newPassword = (password == null || password.isBlank()) ? existing.password() : password;
        Player updated = new Player(id, newUsername, newPassword, existing.role(), wins);
        byId.put(id, updated);
        byUsername.put(normalize(newUsername), updated);
        return true;
    }

    @Override
    public boolean deletePlayer(int id) {
        Player existing = byId.remove(id);
        if (existing == null) {
            return false;
        }
        byUsername.remove(normalize(existing.username()));
        return true;
    }

    @Override
    public boolean incrementWins(String username) {
        Player existing = findByUsername(username);
        if (existing == null) {
            return false;
        }
        Player updated = new Player(
                existing.id(),
                existing.username(),
                existing.password(),
                existing.role(),
                existing.wins() + 1
        );
        byId.put(updated.id(), updated);
        byUsername.put(normalize(updated.username()), updated);
        return true;
    }

    @Override
    public List<Player> findTopPlayersByWins(int limit) {
        List<Player> sorted = new ArrayList<>(findAllPlayers());
        sorted.sort(Comparator.comparingInt(Player::wins).reversed()
                .thenComparing(Player::username));
        if (sorted.size() <= limit) {
            return sorted;
        }
        return sorted.subList(0, limit);
    }

    private static String normalize(String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }
}
