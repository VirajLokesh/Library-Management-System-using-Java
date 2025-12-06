package com.librarymanagement.dao;

import com.librarymanagement.config.DatabaseConfig;
import com.librarymanagement.model.Role;
import com.librarymanagement.model.User;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * JDBC implementation of UserDAO.
 * Handles all database operations for User entity.
 */
public class UserDAOImpl implements UserDAO {
    private static final String INSERT_USER = 
        "INSERT INTO users (username, password_hash, role, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
    
    private static final String SELECT_BY_ID = 
        "SELECT id, username, password_hash, role, created_at, updated_at FROM users WHERE id = ?";
    
    private static final String SELECT_BY_USERNAME = 
        "SELECT id, username, password_hash, role, created_at, updated_at FROM users WHERE username = ?";
    
    private static final String UPDATE_USER = 
        "UPDATE users SET username = ?, password_hash = ?, role = ?, updated_at = ? WHERE id = ?";
    
    private static final String DELETE_USER = 
        "DELETE FROM users WHERE id = ?";
    
    private static final String CHECK_USERNAME_EXISTS = 
        "SELECT COUNT(*) FROM users WHERE username = ?";

    @Override
    public User create(User user) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
            
            LocalDateTime now = LocalDateTime.now();
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole().name());
            stmt.setTimestamp(4, Timestamp.valueOf(now));
            stmt.setTimestamp(5, Timestamp.valueOf(now));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                    user.setCreatedAt(now);
                    user.setUpdatedAt(now);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            
            return user;
        }
    }

    @Override
    public User findById(Integer id) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
                return null;
            }
        }
    }

    @Override
    public User findByUsername(String username) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_USERNAME)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
                return null;
            }
        }
    }

    @Override
    public boolean usernameExists(String username) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_USERNAME_EXISTS)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }

    @Override
    public boolean update(User user) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_USER)) {
            
            LocalDateTime now = LocalDateTime.now();
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole().name());
            stmt.setTimestamp(4, Timestamp.valueOf(now));
            stmt.setInt(5, user.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                user.setUpdatedAt(now);
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean delete(Integer id) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_USER)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Maps a ResultSet row to a User object.
     *
     * @param rs the ResultSet
     * @return User object
     * @throws SQLException if mapping fails
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(Role.valueOf(rs.getString("role")));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return user;
    }
}

