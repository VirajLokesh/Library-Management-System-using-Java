package com.librarymanagement.service;

import com.librarymanagement.dao.UserDAO;
import com.librarymanagement.dao.UserDAOImpl;
import com.librarymanagement.exception.AuthenticationException;
import com.librarymanagement.model.Role;
import com.librarymanagement.model.User;
import com.librarymanagement.util.PasswordUtils;

/**
 * Service class for authentication operations.
 * Handles user login and authentication logic.
 */
public class AuthService {
    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAOImpl();
    }

    /**
     * Constructor for dependency injection (useful for testing).
     *
     * @param userDAO the UserDAO implementation
     */
    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Authenticates a user with username and password.
     *
     * @param username the username
     * @param password the plain text password
     * @return the authenticated User object
     * @throws AuthenticationException if authentication fails
     * @throws Exception if database operation fails
     */
    public User login(String username, String password) throws AuthenticationException, Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Password cannot be empty");
        }

        User user = userDAO.findByUsername(username.trim());
        if (user == null) {
            throw new AuthenticationException("Invalid username or password");
        }

        if (!PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
            throw new AuthenticationException("Invalid username or password");
        }

        return user;
    }

    /**
     * Checks if the current user has the required role.
     *
     * @param user the user to check
     * @param requiredRole the required role
     * @return true if user has the required role, false otherwise
     */
    public boolean hasRole(User user, Role requiredRole) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        return user.getRole() == requiredRole;
    }

    /**
     * Checks if the current user is an admin.
     *
     * @param user the user to check
     * @return true if user is admin, false otherwise
     */
    public boolean isAdmin(User user) {
        return hasRole(user, Role.ADMIN);
    }

    /**
     * Checks if the current user is a librarian or admin.
     *
     * @param user the user to check
     * @return true if user is librarian or admin, false otherwise
     */
    public boolean isLibrarianOrAdmin(User user) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        return user.getRole() == Role.LIBRARIAN || user.getRole() == Role.ADMIN;
    }
}

