package com.wordy.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Persists client connection settings so host/port can be changed without editing code.
 * File: wordy-client.properties in the working directory.
 */
public final class ClientConfig {

    public static final String FILE_NAME = "wordy-client.properties";
    private static final String KEY_HOST = "server.host";
    private static final String KEY_PORT = "server.port";

    private ClientConfig() {
    }

    public record Settings(String host, int port) {
    }

    public static Settings load() {
        Properties props = new Properties();
        Path file = Path.of(FILE_NAME);
        if (Files.isRegularFile(file)) {
            try (InputStream in = Files.newInputStream(file)) {
                props.load(in);
            } catch (IOException ignored) {
                // use defaults below
            }
        }
        String host = props.getProperty(KEY_HOST, "localhost");
        int port = EndpointConfig.DEFAULT_PORT;
        String portRaw = props.getProperty(KEY_PORT);
        if (portRaw != null && !portRaw.isBlank()) {
            try {
                port = Integer.parseInt(portRaw.trim());
            } catch (NumberFormatException ignored) {
                // keep default
            }
        }
        return new Settings(host.trim(), port);
    }

    public static void save(String host, int port) throws IOException {
        Properties props = new Properties();
        props.setProperty(KEY_HOST, host.trim());
        props.setProperty(KEY_PORT, String.valueOf(port));
        Path file = Path.of(FILE_NAME);
        try (var out = Files.newOutputStream(file)) {
            props.store(out, "Wordy client connection settings");
        }
    }
}
