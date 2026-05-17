package com.wordy.common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Shared gRPC address for server + Swing clients.
 * <p>
 * Clients read {@link #clientPort()} which prefers env, then {@value #PORT_FILE_NAME} in the
 * working directory (written by the server when it binds).
 */
public final class EndpointConfig {

    public static final int DEFAULT_PORT = 9090;
    private static final String PORT_FILE_NAME = ".wordy-grpc-port";

    private EndpointConfig() {
    }

    public static String host() {
        String h = System.getenv("WORDY_SERVER_HOST");
        if (h != null && !h.isBlank()) {
            return h.trim();
        }
        return ClientConfig.load().host();
    }

    /**
     * Port the server should try first (env or default).
     */
    public static int serverPreferredPort() {
        Integer fromEnv = parseEnvPort("WORDY_SERVER_PORT");
        if (fromEnv != null) {
            return fromEnv;
        }
        fromEnv = parseEnvPort("WORDY_PORT");
        return fromEnv != null ? fromEnv : DEFAULT_PORT;
    }

    /**
     * Port clients connect to: explicit env wins, else {@value #PORT_FILE_NAME}, else default.
     */
    public static int clientPort() {
        Integer fromEnv = parseEnvPort("WORDY_SERVER_PORT");
        if (fromEnv != null) {
            return fromEnv;
        }
        fromEnv = parseEnvPort("WORDY_PORT");
        if (fromEnv != null) {
            return fromEnv;
        }
        int fromConfig = ClientConfig.load().port();
        if (Files.isRegularFile(Path.of(ClientConfig.FILE_NAME))) {
            return fromConfig;
        }
        Path file = Path.of(PORT_FILE_NAME);
        if (Files.isRegularFile(file)) {
            try {
                String line = Files.readString(file, StandardCharsets.UTF_8).trim();
                if (!line.isEmpty()) {
                    return Integer.parseInt(line);
                }
            } catch (IOException | NumberFormatException ignored) {
                // fall through
            }
        }
        return DEFAULT_PORT;
    }

    public static void publishBoundPort(int port) throws IOException {
        Files.writeString(Path.of(PORT_FILE_NAME), String.valueOf(port), StandardCharsets.UTF_8);
    }

    public static void clearPublishedPort() {
        try {
            Files.deleteIfExists(Path.of(PORT_FILE_NAME));
        } catch (IOException ignored) {
        }
    }

    private static Integer parseEnvPort(String key) {
        String raw = System.getenv(key);
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
