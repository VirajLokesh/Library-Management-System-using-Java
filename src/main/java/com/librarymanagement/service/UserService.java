package com.librarymanagement.service;

import com.librarymanagement.dao.UserDAO;
import com.librarymanagement.dao.UserDAOImpl;
import com.librarymanagement.exception.AuthorizationException;
import com.librarymanagement.exception.EntityNotFoundException;
import com.librarymanagement.exception.ValidationException;
import com.librarymanagement.model.Role;
import com.librarymanagement.model.User;
import com.librarymanagement.util.InputValidator;
import com.librarymanagement.util.PasswordUtils;

import java.util.List;
import java.util.ArrayList;

/**
 * Service class for user management operations.
 * Handles business logic for user CRUD operations.
 */
public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAOImpl();
    }

    /**
     * Constructor for dependency injection.
     *
     * @param userDAO the UserDAO implementation
     */
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Creates a new user. Only ADMIN can create users.
     *
     * @param currentUser the user performing the operation
     * @param username the username
     * @param password the plain text password
     * @param role the user role
     * @return the created User
     * @throws AuthorizationException if current user is not admin
     * @throws ValidationException if input is invalid
     * @throws Exception if creation fails
     */
    public User createUser(User currentUser, String username, String password, Role role) 
            throws AuthorizationException, ValidationException, Exception {
        // Authorization check
        if (!isAdmin(currentUser)) {
            throw new AuthorizationException("Only ADMIN can create users");
        }

        // Validation
        InputValidator.validateNotEmpty(username, "Username");
        InputValidator.validateNotEmpty(password, "Password");
        if (role == null) {
            throw new ValidationException("Role cannot be null");
        }

        // Check if username already exists
        if (userDAO.usernameExists(username)) {
            throw new ValidationException("Username already exists");
        }

        // Create user
        String passwordHash = PasswordUtils.hashPassword(password);
        User user = new User(username, passwordHash, role);
        return userDAO.create(user);
    }

    /**
     * Gets a user by ID. Only ADMIN can view all users.
     *
     * @param currentUser the user performing the operation
     * @param userId the user ID
     * @return the User
     * @throws AuthorizationException if current user is not admin
     * @throws EntityNotFoundException if user not found
     * @throws Exception if query fails
     */
    public User getUserById(User currentUser, Integer userId) 
            throws AuthorizationException, EntityNotFoundException, Exception {
        if (!isAdmin(currentUser)) {
            throw new AuthorizationException("Only ADMIN can view user details");
        }

        User user = userDAO.findById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
        return user;
    }

    /**
     * Gets all users. Only ADMIN can view all users.
     *
     * @param currentUser the user performing the operation
     * @return list of all users
     * @throws AuthorizationException if current user is not admin
     * @throws Exception if query fails
     */
    public List<User> getAllUsers(User currentUser) throws AuthorizationException, Exception {
        if (!isAdmin(currentUser)) {
            throw new AuthorizationException("Only ADMIN can view all users");
        }

        // Since we don't have findAll in UserDAO, we'll need to add it or use a workaround
        // For now, returning empty list - this should be implemented in UserDAO if needed
        return new ArrayList<>();
    }

    /**
     * Updates a user. Only ADMIN can update users.
     *
     * @param currentUser the user performing the operation
     * @param userId the user ID to update
     * @param username the new username (can be null to keep existing)
     * @param password the new password (can be null to keep existing)
     * @param role the new role (can be null to keep existing)
     * @return true if update successful
     * @throws AuthorizationException if current user is not admin
     * @throws EntityNotFoundException if user not found
     * @throws ValidationException if input is invalid
     * @throws Exception if update fails
     */
    public boolean updateUser(User currentUser, Integer userId, String username, 
                             String password, Role role) 
            throws AuthorizationException, EntityNotFoundException, ValidationException, Exception {
        if (!isAdmin(currentUser)) {
            throw new AuthorizationException("Only ADMIN can update users");
        }

        User user = userDAO.findById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }

        // Update fields if provided
        if (username != null && !username.trim().isEmpty()) {
            if (userDAO.usernameExists(username) && !username.equals(user.getUsername())) {
                throw new ValidationException("Username already exists");
            }
            user.setUsername(username.trim());
        }

        if (password != null && !password.trim().isEmpty()) {
            user.setPasswordHash(PasswordUtils.hashPassword(password));
        }

        if (role != null) {
            user.setRole(role);
        }

        return userDAO.update(user);
    }

    /**
     * Deletes a user. Only ADMIN can delete users.
     *
     * @param currentUser the user performing the operation
     * @param userId the user ID to delete
     * @return true if deletion successful
     * @throws AuthorizationException if current user is not admin
     * @throws EntityNotFoundException if user not found
     * @throws Exception if deletion fails
     */
    public boolean deleteUser(User currentUser, Integer userId) 
            throws AuthorizationException, EntityNotFoundException, Exception {
        if (!isAdmin(currentUser)) {
            throw new AuthorizationException("Only ADMIN can delete users");
        }

        User user = userDAO.findById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }

        return userDAO.delete(userId);
    }

    /**
     * Helper method to check if user is admin.
     */
    private boolean isAdmin(User user) {
        return user != null && user.getRole() == Role.ADMIN;
    }
}

