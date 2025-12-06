package com.librarymanagement.dao;

import com.librarymanagement.config.DatabaseConfig;
import com.librarymanagement.model.Book;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of BookDAO.
 * Handles all database operations for Book entity.
 */
public class BookDAOImpl implements BookDAO {
    private static final String INSERT_BOOK = 
        "INSERT INTO books (isbn, title, author, publisher, published_year, total_copies, available_copies, created_at, updated_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_BY_ID = 
        "SELECT id, isbn, title, author, publisher, published_year, total_copies, available_copies, created_at, updated_at " +
        "FROM books WHERE id = ?";
    
    private static final String SELECT_BY_ISBN = 
        "SELECT id, isbn, title, author, publisher, published_year, total_copies, available_copies, created_at, updated_at " +
        "FROM books WHERE isbn = ?";
    
    private static final String SELECT_ALL = 
        "SELECT id, isbn, title, author, publisher, published_year, total_copies, available_copies, created_at, updated_at " +
        "FROM books ORDER BY title";
    
    private static final String SEARCH_BY_TITLE = 
        "SELECT id, isbn, title, author, publisher, published_year, total_copies, available_copies, created_at, updated_at " +
        "FROM books WHERE LOWER(title) LIKE LOWER(?) ORDER BY title";
    
    private static final String SEARCH_BY_AUTHOR = 
        "SELECT id, isbn, title, author, publisher, published_year, total_copies, available_copies, created_at, updated_at " +
        "FROM books WHERE LOWER(author) LIKE LOWER(?) ORDER BY author, title";
    
    private static final String SEARCH_BY_ISBN = 
        "SELECT id, isbn, title, author, publisher, published_year, total_copies, available_copies, created_at, updated_at " +
        "FROM books WHERE isbn LIKE ? ORDER BY isbn";
    
    private static final String UPDATE_BOOK = 
        "UPDATE books SET isbn = ?, title = ?, author = ?, publisher = ?, published_year = ?, " +
        "total_copies = ?, available_copies = ?, updated_at = ? WHERE id = ?";
    
    private static final String DELETE_BOOK = 
        "DELETE FROM books WHERE id = ?";
    
    private static final String CHECK_ISBN_EXISTS = 
        "SELECT COUNT(*) FROM books WHERE isbn = ?";

    @Override
    public Book create(Book book) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_BOOK, Statement.RETURN_GENERATED_KEYS)) {
            
            LocalDateTime now = LocalDateTime.now();
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getPublisher());
            stmt.setInt(5, book.getPublishedYear());
            stmt.setInt(6, book.getTotalCopies());
            stmt.setInt(7, book.getAvailableCopies());
            stmt.setTimestamp(8, Timestamp.valueOf(now));
            stmt.setTimestamp(9, Timestamp.valueOf(now));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating book failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    book.setId(generatedKeys.getInt(1));
                    book.setCreatedAt(now);
                    book.setUpdatedAt(now);
                } else {
                    throw new SQLException("Creating book failed, no ID obtained.");
                }
            }
            
            return book;
        }
    }

    @Override
    public Book findById(Integer id) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBook(rs);
                }
                return null;
            }
        }
    }

    @Override
    public Book findByISBN(String isbn) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ISBN)) {
            
            stmt.setString(1, isbn);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBook(rs);
                }
                return null;
            }
        }
    }

    @Override
    public List<Book> findAll() throws Exception {
        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }
        return books;
    }

    @Override
    public List<Book> searchByTitle(String title) throws Exception {
        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_BY_TITLE)) {
            
            stmt.setString(1, "%" + title + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        }
        return books;
    }

    @Override
    public List<Book> searchByAuthor(String author) throws Exception {
        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_BY_AUTHOR)) {
            
            stmt.setString(1, "%" + author + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        }
        return books;
    }

    @Override
    public List<Book> searchByISBN(String isbn) throws Exception {
        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_BY_ISBN)) {
            
            stmt.setString(1, "%" + isbn + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        }
        return books;
    }

    @Override
    public boolean isbnExists(String isbn) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_ISBN_EXISTS)) {
            
            stmt.setString(1, isbn);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }

    @Override
    public boolean update(Book book) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_BOOK)) {
            
            LocalDateTime now = LocalDateTime.now();
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getPublisher());
            stmt.setInt(5, book.getPublishedYear());
            stmt.setInt(6, book.getTotalCopies());
            stmt.setInt(7, book.getAvailableCopies());
            stmt.setTimestamp(8, Timestamp.valueOf(now));
            stmt.setInt(9, book.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                book.setUpdatedAt(now);
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean delete(Integer id) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_BOOK)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Maps a ResultSet row to a Book object.
     *
     * @param rs the ResultSet
     * @return Book object
     * @throws SQLException if mapping fails
     */
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setPublisher(rs.getString("publisher"));
        book.setPublishedYear(rs.getInt("published_year"));
        book.setTotalCopies(rs.getInt("total_copies"));
        book.setAvailableCopies(rs.getInt("available_copies"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            book.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            book.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return book;
    }
}

