package com.librarymanagement.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages database connections using JDBC.
 * Provides a single connection manager with configurable properties.
 * Supports connection pooling concepts (can be extended later).
 */
public class DatabaseConfig {
    private static final String DEFAULT_CONFIG_FILE = "database.properties";
    private static final String DEFAULT_DB_URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String DEFAULT_DB_USER = "root";
    private static final String DEFAULT_DB_PASSWORD = "root";
    
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;
    
    static {
        loadDatabaseProperties();
    }

    /**
     * Loads database configuration from properties file or uses defaults.
     */
    private static void loadDatabaseProperties() {
        Properties props = new Properties();
        
        try (FileInputStream fis = new FileInputStream(DEFAULT_CONFIG_FILE)) {
            props.load(fis);
            dbUrl = props.getProperty("db.url", DEFAULT_DB_URL);
            dbUser = props.getProperty("db.user", DEFAULT_DB_USER);
            dbPassword = props.getProperty("db.password", DEFAULT_DB_PASSWORD);
        } catch (IOException e) {
            // If properties file doesn't exist, use defaults
            System.out.println("Warning: database.properties not found. Using default configuration.");
            dbUrl = DEFAULT_DB_URL;
            dbUser = DEFAULT_DB_USER;
            dbPassword = DEFAULT_DB_PASSWORD;
        }
    }

    /**
     * Creates and returns a new database connection.
     *
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }

    /**
     * Sets database configuration programmatically.
     *
     * @param url database URL
     * @param user database username
     * @param password database password
     */
    public static void setConfiguration(String url, String user, String password) {
        dbUrl = url;
        dbUser = user;
        dbPassword = password;
    }

    /**
     * Tests the database connection.
     *
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}

