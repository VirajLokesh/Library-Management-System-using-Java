package com.librarymanagement.dao;

import com.librarymanagement.config.DatabaseConfig;
import com.librarymanagement.model.Loan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of LoanDAO.
 * Handles all database operations for Loan entity.
 */
public class LoanDAOImpl implements LoanDAO {
    private static final String INSERT_LOAN = 
        "INSERT INTO loans (book_id, member_id, issue_date, due_date, return_date, fine_amount, status) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_BY_ID = 
        "SELECT id, book_id, member_id, issue_date, due_date, return_date, fine_amount, status " +
        "FROM loans WHERE id = ?";
    
    private static final String SELECT_ACTIVE_BY_MEMBER = 
        "SELECT id, book_id, member_id, issue_date, due_date, return_date, fine_amount, status " +
        "FROM loans WHERE member_id = ? AND status IN ('ISSUED', 'OVERDUE') ORDER BY due_date";
    
    private static final String SELECT_ALL_BY_MEMBER = 
        "SELECT id, book_id, member_id, issue_date, due_date, return_date, fine_amount, status " +
        "FROM loans WHERE member_id = ? ORDER BY issue_date DESC";
    
    private static final String SELECT_ACTIVE_BY_BOOK_AND_MEMBER = 
        "SELECT id, book_id, member_id, issue_date, due_date, return_date, fine_amount, status " +
        "FROM loans WHERE book_id = ? AND member_id = ? AND status IN ('ISSUED', 'OVERDUE')";
    
    private static final String SELECT_OVERDUE = 
        "SELECT id, book_id, member_id, issue_date, due_date, return_date, fine_amount, status " +
        "FROM loans WHERE status = 'OVERDUE' OR (status = 'ISSUED' AND due_date < CURDATE()) " +
        "ORDER BY due_date";
    
    private static final String SELECT_ALL_ACTIVE = 
        "SELECT id, book_id, member_id, issue_date, due_date, return_date, fine_amount, status " +
        "FROM loans WHERE status IN ('ISSUED', 'OVERDUE') ORDER BY issue_date DESC";
    
    private static final String COUNT_ACTIVE_BY_MEMBER = 
        "SELECT COUNT(*) FROM loans WHERE member_id = ? AND status IN ('ISSUED', 'OVERDUE')";
    
    private static final String UPDATE_LOAN = 
        "UPDATE loans SET book_id = ?, member_id = ?, issue_date = ?, due_date = ?, " +
        "return_date = ?, fine_amount = ?, status = ? WHERE id = ?";
    
    private static final String DELETE_LOAN = 
        "DELETE FROM loans WHERE id = ?";

    @Override
    public Loan create(Loan loan) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_LOAN, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, loan.getBookId());
            stmt.setInt(2, loan.getMemberId());
            stmt.setDate(3, Date.valueOf(loan.getIssueDate()));
            stmt.setDate(4, Date.valueOf(loan.getDueDate()));
            
            if (loan.getReturnDate() != null) {
                stmt.setDate(5, Date.valueOf(loan.getReturnDate()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            stmt.setDouble(6, loan.getFineAmount());
            stmt.setString(7, loan.getStatus().name());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating loan failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    loan.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating loan failed, no ID obtained.");
                }
            }
            
            return loan;
        }
    }

    @Override
    public Loan findById(Integer id) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLoan(rs);
                }
                return null;
            }
        }
    }

    @Override
    public List<Loan> findActiveLoansByMember(Integer memberId) throws Exception {
        List<Loan> loans = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ACTIVE_BY_MEMBER)) {
            
            stmt.setInt(1, memberId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }
        }
        return loans;
    }

    @Override
    public List<Loan> findAllLoansByMember(Integer memberId) throws Exception {
        List<Loan> loans = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_BY_MEMBER)) {
            
            stmt.setInt(1, memberId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }
        }
        return loans;
    }

    @Override
    public Loan findActiveLoanByBookAndMember(Integer bookId, Integer memberId) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ACTIVE_BY_BOOK_AND_MEMBER)) {
            
            stmt.setInt(1, bookId);
            stmt.setInt(2, memberId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLoan(rs);
                }
                return null;
            }
        }
    }

    @Override
    public List<Loan> findOverdueLoans() throws Exception {
        List<Loan> loans = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_OVERDUE);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        }
        return loans;
    }

    @Override
    public List<Loan> findAllActiveLoans() throws Exception {
        List<Loan> loans = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_ACTIVE);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        }
        return loans;
    }

    @Override
    public int countActiveLoansByMember(Integer memberId) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_ACTIVE_BY_MEMBER)) {
            
            stmt.setInt(1, memberId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }

    @Override
    public boolean update(Loan loan) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_LOAN)) {
            
            stmt.setInt(1, loan.getBookId());
            stmt.setInt(2, loan.getMemberId());
            stmt.setDate(3, Date.valueOf(loan.getIssueDate()));
            stmt.setDate(4, Date.valueOf(loan.getDueDate()));
            
            if (loan.getReturnDate() != null) {
                stmt.setDate(5, Date.valueOf(loan.getReturnDate()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            stmt.setDouble(6, loan.getFineAmount());
            stmt.setString(7, loan.getStatus().name());
            stmt.setInt(8, loan.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer id) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_LOAN)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Maps a ResultSet row to a Loan object.
     *
     * @param rs the ResultSet
     * @return Loan object
     * @throws SQLException if mapping fails
     */
    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getInt("id"));
        loan.setBookId(rs.getInt("book_id"));
        loan.setMemberId(rs.getInt("member_id"));
        
        Date issueDate = rs.getDate("issue_date");
        if (issueDate != null) {
            loan.setIssueDate(issueDate.toLocalDate());
        }
        
        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            loan.setDueDate(dueDate.toLocalDate());
        }
        
        Date returnDate = rs.getDate("return_date");
        if (returnDate != null) {
            loan.setReturnDate(returnDate.toLocalDate());
        }
        
        loan.setFineAmount(rs.getDouble("fine_amount"));
        
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            loan.setStatus(Loan.LoanStatus.valueOf(statusStr));
        }
        
        return loan;
    }
}

