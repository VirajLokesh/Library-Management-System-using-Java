package com.librarymanagement.dao;

/**
 * Base interface for Data Access Objects.
 * Provides common CRUD operations that can be extended by specific DAOs.
 *
 * @param <T> the entity type
 * @param <ID> the ID type (usually Integer)
 */
public interface BaseDAO<T, ID> {
    /**
     * Creates a new entity in the database.
     *
     * @param entity the entity to create
     * @return the created entity with generated ID
     * @throws Exception if creation fails
     */
    T create(T entity) throws Exception;

    /**
     * Finds an entity by its ID.
     *
     * @param id the entity ID
     * @return the entity if found, null otherwise
     * @throws Exception if query fails
     */
    T findById(ID id) throws Exception;

    /**
     * Updates an existing entity in the database.
     *
     * @param entity the entity to update
     * @return true if update was successful, false otherwise
     * @throws Exception if update fails
     */
    boolean update(T entity) throws Exception;

    /**
     * Deletes an entity by its ID.
     *
     * @param id the entity ID
     * @return true if deletion was successful, false otherwise
     * @throws Exception if deletion fails
     */
    boolean delete(ID id) throws Exception;
}

