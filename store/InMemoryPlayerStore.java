package com.wordy.server.store;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * In-memory player store used when MySQL is not configured.
 */
public class InMemoryPlayerStore {

    private static final InMemoryPlayerStore INSTANCE = new InMemoryPlayerStore();

    public record PlayerRecord(int id, String username, String password, String role, int wins) {}

    private final Map<Integer, PlayerRecord> byId = new ConcurrentHashMap<>();
    private final Map<String, PlayerRecord> byUsername = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public static InMemoryPlayerStore getInstance() {
        return INSTANCE;
    }

    private InMemoryPlayerStore() {
        seed("admin", "1234", "ADMIN", 0);
        seed("player1", "1234", "PLAYER", 0);
        seed("player2", "1234", "PLAYER", 0);
    }

    private void seed(String username, String password, String role, int wins) {
        int id = nextId.getAndIncrement();
        PlayerRecord record = new PlayerRecord(id, username, password, role, wins);
        byId.put(id, record);
        byUsername.put(normalize(username), record);
    }

    public PlayerRecord getPlayerByUsername(String username) {
        if (username == null) {
            return null;
        }
        return byUsername.get(normalize(username));
    }

    public PlayerRecord getPlayerById(int id) {
        return byId.get(id);
    }

    public List<PlayerRecord> getAllPlayers() {
        return byId.values().stream()
                .filter(p -> "PLAYER".equals(p.role()))
                .sorted(Comparator.comparing(PlayerRecord::username))
                .collect(Collectors.toList());
    }

    public List<PlayerRecord> searchPlayers(String keyword) {
        String lower = keyword.toLowerCase(Locale.ROOT);
        return getAllPlayers().stream()
                .filter(p -> p.username().toLowerCase(Locale.ROOT).contains(lower))
                .collect(Collectors.toList());
    }

    public boolean createPlayer(String username, String password, String role) {
        if (username == null || byUsername.containsKey(normalize(username))) {
            return false;
        }
        int id = nextId.getAndIncrement();
        PlayerRecord record = new PlayerRecord(id, username.trim(), password, role, 0);
        byId.put(id, record);
        byUsername.put(normalize(username), record);
        return true;
    }

    public boolean updatePlayer(int id, String username, String password, int wins) {
        PlayerRecord existing = byId.get(id);
        if (existing == null || !"PLAYER".equals(existing.role())) {
            return false;
        }

        byUsername.remove(normalize(existing.username()));

        String newUsername = username.trim();
        String newPassword = (password == null || password.isBlank()) ? existing.password() : password;
        PlayerRecord updated = new PlayerRecord(id, newUsername, newPassword, existing.role(), wins);
        byId.put(id, updated);
        byUsername.put(normalize(newUsername), updated);
        return true;
    }

    public boolean deletePlayer(int id) {
        PlayerRecord existing = byId.remove(id);
        if (existing == null) {
            return false;
        }
        byUsername.remove(normalize(existing.username()));
        return true;
    }

    public boolean incrementWins(String username) {
        PlayerRecord existing = getPlayerByUsername(username);
        if (existing == null) {
            return false;
        }
        PlayerRecord updated = new PlayerRecord(
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

    public List<PlayerRecord> getTopPlayersByWins(int limit) {
        List<PlayerRecord> sorted = new ArrayList<>(getAllPlayers());
        sorted.sort(Comparator.comparingInt(PlayerRecord::wins).reversed()
                .thenComparing(PlayerRecord::username));
        if (sorted.size() <= limit) {
            return sorted;
        }
        return sorted.subList(0, limit);
    }

    private static String normalize(String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }
}
