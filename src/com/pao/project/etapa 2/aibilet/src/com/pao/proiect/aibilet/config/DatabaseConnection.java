package com.pao.proiect.aibilet.config;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;

    private final DatabaseConfig config;

    private DatabaseConnection() throws IOException {
        this.config = new DatabaseConfig();
    }

    public static synchronized DatabaseConnection getInstance() throws IOException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }

        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                config.getUrl(),
                config.getUser(),
                config.getPassword()
        );
    }
}
