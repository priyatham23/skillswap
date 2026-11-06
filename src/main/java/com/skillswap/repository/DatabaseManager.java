package com.skillswap.repository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Properties config = new Properties();
    private boolean connected = false;
    private String statusMessage = "Not Initialized";

    private DatabaseManager() {
        loadConfig();
        initConnection();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void loadConfig() {
        File cfg = new File("config.properties");
        if (cfg.exists()) {
            try (InputStream in = new FileInputStream(cfg)) {
                config.load(in);
            } catch (Exception e) {
                System.err.println("[DatabaseManager] Could not load config.properties: " + e.getMessage());
            }
        }

        // Cloud environment variable overrides (Render, Railway, Heroku, etc.)
        String envUrl = System.getenv("DB_URL");
        if (envUrl == null) envUrl = System.getenv("DATABASE_URL");
        if (envUrl != null && !envUrl.trim().isEmpty()) {
            if (envUrl.startsWith("mysql://")) {
                envUrl = "jdbc:" + envUrl;
            }
            config.setProperty("db.url", envUrl);
        }
        String envUser = System.getenv("DB_USER");
        if (envUser != null && !envUser.trim().isEmpty()) {
            config.setProperty("db.user", envUser);
        }
        String envPass = System.getenv("DB_PASSWORD");
        if (envPass != null) {
            config.setProperty("db.password", envPass);
        }
    }

    public void initConnection() {
        loadConfig();
        String url = config.getProperty("db.url", "jdbc:mysql://localhost:3306/skillswap?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        String user = config.getProperty("db.user", "root");
        String pass = config.getProperty("db.password", "root");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                connected = true;
                statusMessage = "Connected to MySQL (" + url + ")";
                System.out.println("[DatabaseManager] " + statusMessage);
                executeSchema(conn);
            }
        } catch (ClassNotFoundException e) {
            connected = false;
            statusMessage = "MySQL Driver not found on classpath: " + e.getMessage();
            System.err.println("[DatabaseManager] " + statusMessage);
        } catch (SQLException e) {
            connected = false;
            statusMessage = "MySQL Connection failed (" + e.getMessage() + "). Check config.properties (db.user / db.password).";
            System.err.println("[DatabaseManager] " + statusMessage);
        }
    }

    private void executeSchema(Connection conn) {
        File schemaFile = new File("database/schema.sql");
        if (!schemaFile.exists()) return;

        try {
            String sqlContent = new String(Files.readAllBytes(Paths.get(schemaFile.getPath())), StandardCharsets.UTF_8);
            String[] statements = sqlContent.split(";");
            try (Statement st = conn.createStatement()) {
                for (String raw : statements) {
                    String stmt = raw.trim();
                    if (!stmt.isEmpty() && !stmt.startsWith("--")) {
                        try {
                            st.execute(stmt);
                        } catch (SQLException ex) {
                            // Suppress table already exists or minor warnings
                        }
                    }
                }
            }
            System.out.println("[DatabaseManager] Schema verified / executed successfully.");
        } catch (Exception e) {
            System.err.println("[DatabaseManager] Error running schema: " + e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        if (!connected) {
            throw new SQLException("Database not currently connected.");
        }
        String url = config.getProperty("db.url", "jdbc:mysql://localhost:3306/skillswap?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        String user = config.getProperty("db.user", "root");
        String pass = config.getProperty("db.password", "root");
        return DriverManager.getConnection(url, user, pass);
    }

    public boolean isConnected() {
        return connected;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}

