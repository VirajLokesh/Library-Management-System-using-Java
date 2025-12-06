package com.librarymanagement.dao;

import com.librarymanagement.model.Book;
import java.util.List;

/**
 * Data Access Object interface for Book entity.
 * Extends BaseDAO and adds book-specific methods.
 */
public interface BookDAO extends BaseDAO<Book, Integer> {
    /**
     * Finds a book by ISBN.
     *
     * @param isbn the ISBN
     * @return the Book if found, null otherwise
     * @throws Exception if query fails
     */
    Book findByISBN(String isbn) throws Exception;

    /**
     * Searches books by title (case-insensitive partial match).
     *
     * @param title the title search term
     * @return list of matching books
     * @throws Exception if query fails
     */
    List<Book> searchByTitle(String title) throws Exception;

    /**
     * Searches books by author (case-insensitive partial match).
     *
     * @param author the author search term
     * @return list of matching books
     * @throws Exception if query fails
     */
    List<Book> searchByAuthor(String author) throws Exception;

    /**
     * Searches books by ISBN (partial match).
     *
     * @param isbn the ISBN search term
     * @return list of matching books
     * @throws Exception if query fails
     */
    List<Book> searchByISBN(String isbn) throws Exception;

    /**
     * Gets all books.
     *
     * @return list of all books
     * @throws Exception if query fails
     */
    List<Book> findAll() throws Exception;

    /**
     * Checks if an ISBN already exists.
     *
     * @param isbn the ISBN to check
     * @return true if ISBN exists, false otherwise
     * @throws Exception if query fails
     */
    boolean isbnExists(String isbn) throws Exception;
}

