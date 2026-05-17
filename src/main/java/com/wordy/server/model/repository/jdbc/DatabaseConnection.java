package com.wordy.server.model.repository.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/wordy?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private static Connection connection;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DatabaseConnection() {
    }

    public static String getUrl() {
        String env = System.getenv("WORDY_DB_URL");
        return env != null && !env.isBlank() ? env : DEFAULT_URL;
    }

    public static String getUser() {
        String env = System.getenv("WORDY_DB_USER");
        return env != null ? env : "root";
    }

    public static String getPassword() {
        String env = System.getenv("WORDY_DB_PASSWORD");
        return env != null ? env : "";
    }

    public static boolean isAvailable() {
        try (Connection test = DriverManager.getConnection(getUrl(), getUser(), getPassword())) {
            return test.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(getUrl(), getUser(), getPassword());
        }
        return connection;
    }
}
