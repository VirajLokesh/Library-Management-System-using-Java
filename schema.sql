-- Library Management System Database Schema
-- MySQL 8.0+

-- Create database
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- Drop tables if they exist (in reverse order of dependencies)
DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS users;

-- Create users table
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'LIBRARIAN', 'MEMBER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create books table
CREATE TABLE books (
    id INT PRIMARY KEY AUTO_INCREMENT,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    publisher VARCHAR(255),
    published_year INT,
    total_copies INT NOT NULL DEFAULT 1,
    available_copies INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_isbn (isbn),
    INDEX idx_title (title),
    INDEX idx_author (author)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create members table
CREATE TABLE members (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(20),
    join_date DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create loans table
CREATE TABLE loans (
    id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT NOT NULL,
    member_id INT NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE NULL,
    fine_amount DECIMAL(10, 2) DEFAULT 0.00,
    status ENUM('ISSUED', 'RETURNED', 'OVERDUE') NOT NULL DEFAULT 'ISSUED',
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE RESTRICT,
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE RESTRICT,
    INDEX idx_book_id (book_id),
    INDEX idx_member_id (member_id),
    INDEX idx_due_date (due_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample data

-- Insert users (passwords are hashed using SHA-256)
-- Default passwords:
--   admin: admin123
--   librarian: librarian123
--   member1: member123
--   member2: member123
-- 
-- IMPORTANT: The password hashes below are placeholders.
-- Before using this schema, generate correct password hashes using:
--   java -cp target/classes com.librarymanagement.util.PasswordHashGenerator
-- 
-- Or use any SHA-256 hash generator online/offline.
-- Then replace the hashes below with the generated ones.
-- 
-- WARNING: These are default passwords for testing. Change them in production!
INSERT INTO users (username, password_hash, role) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN'),
('librarian', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'LIBRARIAN'),
('member1', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'MEMBER'),
('member2', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'MEMBER');

-- Insert books
INSERT INTO books (isbn, title, author, publisher, published_year, total_copies, available_copies) VALUES
('978-0134685991', 'Effective Java', 'Joshua Bloch', 'Addison-Wesley', 2018, 5, 5),
('978-0596009205', 'Head First Design Patterns', 'Eric Freeman, Elisabeth Robson', 'O''Reilly Media', 2004, 3, 3),
('978-0132350884', 'Clean Code', 'Robert C. Martin', 'Prentice Hall', 2008, 4, 4),
('978-0201633610', 'Design Patterns', 'Gang of Four', 'Addison-Wesley', 1994, 2, 2),
('978-0596007126', 'Java in a Nutshell', 'David Flanagan', 'O''Reilly Media', 2005, 3, 3),
('978-0132778046', 'Java Concurrency in Practice', 'Brian Goetz', 'Addison-Wesley', 2006, 2, 2),
('978-0596517748', 'The Art of Computer Programming', 'Donald E. Knuth', 'Addison-Wesley', 2011, 1, 1),
('978-0134685992', 'Introduction to Algorithms', 'Thomas H. Cormen', 'MIT Press', 2009, 3, 3);

-- Insert members
INSERT INTO members (user_id, name, email, phone, join_date) VALUES
(3, 'John Doe', 'john.doe@example.com', '555-0101', '2024-01-15'),
(4, 'Jane Smith', 'jane.smith@example.com', '555-0102', '2024-02-01');


