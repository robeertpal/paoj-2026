package com.pao.proiect.aibilet.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final String CONFIG_FILE = "resources/db.properties";

    private final String url;
    private final String user;
    private final String password;

    public DatabaseConfig() throws IOException {
        Properties properties = new Properties();

        try (InputStream inputStream = new FileInputStream(CONFIG_FILE)) {
            properties.load(inputStream);
        }

        this.url = getRequiredProperty(properties, "db.url");
        this.user = getRequiredProperty(properties, "db.user");
        this.password = getRequiredProperty(properties, "db.password");
    }

    private String getRequiredProperty(Properties properties, String key) throws IOException {
        String value = properties.getProperty(key);

        if (value == null || value.trim().isEmpty()) {
            throw new IOException("Lipseste proprietatea obligatorie: " + key);
        }

        return value.trim();
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}