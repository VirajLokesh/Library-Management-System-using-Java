package com.librarymanagement.dao;

import com.librarymanagement.config.DatabaseConfig;
import com.librarymanagement.model.Member;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of MemberDAO.
 * Handles all database operations for Member entity.
 */
public class MemberDAOImpl implements MemberDAO {
    private static final String INSERT_MEMBER = 
        "INSERT INTO members (user_id, name, email, phone, join_date) VALUES (?, ?, ?, ?, ?)";
    
    private static final String SELECT_BY_ID = 
        "SELECT id, user_id, name, email, phone, join_date FROM members WHERE id = ?";
    
    private static final String SELECT_BY_USER_ID = 
        "SELECT id, user_id, name, email, phone, join_date FROM members WHERE user_id = ?";
    
    private static final String SELECT_ALL = 
        "SELECT id, user_id, name, email, phone, join_date FROM members ORDER BY name";
    
    private static final String UPDATE_MEMBER = 
        "UPDATE members SET user_id = ?, name = ?, email = ?, phone = ? WHERE id = ?";
    
    private static final String DELETE_MEMBER = 
        "DELETE FROM members WHERE id = ?";

    @Override
    public Member create(Member member) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_MEMBER, Statement.RETURN_GENERATED_KEYS)) {
            
            LocalDate joinDate = member.getJoinDate();
            if (joinDate == null) {
                joinDate = LocalDate.now();
            }
            
            stmt.setInt(1, member.getUserId());
            stmt.setString(2, member.getName());
            stmt.setString(3, member.getEmail());
            stmt.setString(4, member.getPhone());
            stmt.setDate(5, Date.valueOf(joinDate));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating member failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    member.setId(generatedKeys.getInt(1));
                    member.setJoinDate(joinDate);
                } else {
                    throw new SQLException("Creating member failed, no ID obtained.");
                }
            }
            
            return member;
        }
    }

    @Override
    public Member findById(Integer id) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMember(rs);
                }
                return null;
            }
        }
    }

    @Override
    public Member findByUserId(Integer userId) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_USER_ID)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMember(rs);
                }
                return null;
            }
        }
    }

    @Override
    public List<Member> findAll() throws Exception {
        List<Member> members = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
        }
        return members;
    }

    @Override
    public boolean update(Member member) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_MEMBER)) {
            
            stmt.setInt(1, member.getUserId());
            stmt.setString(2, member.getName());
            stmt.setString(3, member.getEmail());
            stmt.setString(4, member.getPhone());
            stmt.setInt(5, member.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer id) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_MEMBER)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Maps a ResultSet row to a Member object.
     *
     * @param rs the ResultSet
     * @return Member object
     * @throws SQLException if mapping fails
     */
    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getInt("id"));
        member.setUserId(rs.getInt("user_id"));
        member.setName(rs.getString("name"));
        member.setEmail(rs.getString("email"));
        member.setPhone(rs.getString("phone"));
        
        Date joinDate = rs.getDate("join_date");
        if (joinDate != null) {
            member.setJoinDate(joinDate.toLocalDate());
        }
        
        return member;
    }
}

