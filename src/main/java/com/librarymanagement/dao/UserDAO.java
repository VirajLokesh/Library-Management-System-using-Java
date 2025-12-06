package com.librarymanagement.dao;

import com.librarymanagement.model.User;

/**
 * Data Access Object interface for User entity.
 * Extends BaseDAO and adds user-specific methods.
 */
public interface UserDAO extends BaseDAO<User, Integer> {
    /**
     * Finds a user by username.
     *
     * @param username the username
     * @return the User if found, null otherwise
     * @throws Exception if query fails
     */
    User findByUsername(String username) throws Exception;

    /**
     * Checks if a username already exists.
     *
     * @param username the username to check
     * @return true if username exists, false otherwise
     * @throws Exception if query fails
     */
    boolean usernameExists(String username) throws Exception;
}

