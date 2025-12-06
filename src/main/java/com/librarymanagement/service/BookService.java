package com.librarymanagement.service;

import com.librarymanagement.dao.BookDAO;
import com.librarymanagement.dao.BookDAOImpl;
import com.librarymanagement.exception.AuthorizationException;
import com.librarymanagement.exception.EntityNotFoundException;
import com.librarymanagement.exception.ValidationException;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Role;
import com.librarymanagement.model.User;
import com.librarymanagement.util.InputValidator;

import java.util.List;

/**
 * Service class for book management operations.
 * Handles business logic for book CRUD operations and search.
 */
public class BookService {
    private final BookDAO bookDAO;

    public BookService() {
        this.bookDAO = new BookDAOImpl();
    }

    /**
     * Constructor for dependency injection.
     *
     * @param bookDAO the BookDAO implementation
     */
    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    /**
     * Creates a new book. Only ADMIN and LIBRARIAN can create books.
     *
     * @param currentUser the user performing the operation
     * @param isbn the ISBN
     * @param title the book title
     * @param author the author name
     * @param publisher the publisher name
     * @param publishedYear the published year
     * @param totalCopies the total number of copies
     * @return the created Book
     * @throws AuthorizationException if current user is not authorized
     * @throws ValidationException if input is invalid
     * @throws Exception if creation fails
     */
    public Book createBook(User currentUser, String isbn, String title, String author, 
                          String publisher, Integer publishedYear, Integer totalCopies) 
            throws AuthorizationException, ValidationException, Exception {
        // Authorization check
        if (!isLibrarianOrAdmin(currentUser)) {
            throw new AuthorizationException("Only ADMIN and LIBRARIAN can create books");
        }

        // Validation
        InputValidator.validateISBN(isbn);
        InputValidator.validateNotEmpty(title, "Title");
        InputValidator.validateNotEmpty(author, "Author");
        InputValidator.validatePositive(totalCopies, "Total copies");
        if (publishedYear != null) {
            InputValidator.validateYear(publishedYear);
        }

        // Check if ISBN already exists
        if (bookDAO.isbnExists(isbn)) {
            throw new ValidationException("Book with ISBN " + isbn + " already exists");
        }

        // Create book
        Book book = new Book(isbn, title, author, publisher, publishedYear, totalCopies);
        return bookDAO.create(book);
    }

    /**
     * Gets a book by ID.
     *
     * @param bookId the book ID
     * @return the Book
     * @throws EntityNotFoundException if book not found
     * @throws Exception if query fails
     */
    public Book getBookById(Integer bookId) throws EntityNotFoundException, Exception {
        Book book = bookDAO.findById(bookId);
        if (book == null) {
            throw new EntityNotFoundException("Book not found with ID: " + bookId);
        }
        return book;
    }

    /**
     * Gets all books.
     *
     * @return list of all books
     * @throws Exception if query fails
     */
    public List<Book> getAllBooks() throws Exception {
        return bookDAO.findAll();
    }

    /**
     * Searches books by title, author, or ISBN.
     *
     * @param searchTerm the search term
     * @param searchType the type of search (title, author, isbn)
     * @return list of matching books
     * @throws ValidationException if search type is invalid
     * @throws Exception if query fails
     */
    public List<Book> searchBooks(String searchTerm, String searchType) 
            throws ValidationException, Exception {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new ValidationException("Search term cannot be empty");
        }

        String term = searchTerm.trim();
        switch (searchType.toLowerCase()) {
            case "title":
                return bookDAO.searchByTitle(term);
            case "author":
                return bookDAO.searchByAuthor(term);
            case "isbn":
                return bookDAO.searchByISBN(term);
            default:
                throw new ValidationException("Invalid search type. Use: title, author, or isbn");
        }
    }

    /**
     * Updates a book. Only ADMIN and LIBRARIAN can update books.
     *
     * @param currentUser the user performing the operation
     * @param bookId the book ID to update
     * @param isbn the new ISBN (can be null)
     * @param title the new title (can be null)
     * @param author the new author (can be null)
     * @param publisher the new publisher (can be null)
     * @param publishedYear the new published year (can be null)
     * @param totalCopies the new total copies (can be null)
     * @return true if update successful
     * @throws AuthorizationException if current user is not authorized
     * @throws EntityNotFoundException if book not found
     * @throws ValidationException if input is invalid
     * @throws Exception if update fails
     */
    public boolean updateBook(User currentUser, Integer bookId, String isbn, String title, 
                             String author, String publisher, Integer publishedYear, Integer totalCopies) 
            throws AuthorizationException, EntityNotFoundException, ValidationException, Exception {
        if (!isLibrarianOrAdmin(currentUser)) {
            throw new AuthorizationException("Only ADMIN and LIBRARIAN can update books");
        }

        Book book = bookDAO.findById(bookId);
        if (book == null) {
            throw new EntityNotFoundException("Book not found with ID: " + bookId);
        }

        // Update fields if provided
        if (isbn != null && !isbn.trim().isEmpty()) {
            InputValidator.validateISBN(isbn);
            if (bookDAO.isbnExists(isbn) && !isbn.equals(book.getIsbn())) {
                throw new ValidationException("Book with ISBN " + isbn + " already exists");
            }
            book.setIsbn(isbn.trim());
        }

        if (title != null && !title.trim().isEmpty()) {
            book.setTitle(title.trim());
        }

        if (author != null && !author.trim().isEmpty()) {
            book.setAuthor(author.trim());
        }

        if (publisher != null) {
            book.setPublisher(publisher.trim());
        }

        if (publishedYear != null) {
            InputValidator.validateYear(publishedYear);
            book.setPublishedYear(publishedYear);
        }

        if (totalCopies != null) {
            InputValidator.validatePositive(totalCopies, "Total copies");
            // Adjust available copies if total copies changed
            int difference = totalCopies - book.getTotalCopies();
            book.setTotalCopies(totalCopies);
            book.setAvailableCopies(Math.max(0, book.getAvailableCopies() + difference));
        }

        return bookDAO.update(book);
    }

    /**
     * Deletes a book. Only ADMIN and LIBRARIAN can delete books.
     *
     * @param currentUser the user performing the operation
     * @param bookId the book ID to delete
     * @return true if deletion successful
     * @throws AuthorizationException if current user is not authorized
     * @throws EntityNotFoundException if book not found
     * @throws Exception if deletion fails
     */
    public boolean deleteBook(User currentUser, Integer bookId) 
            throws AuthorizationException, EntityNotFoundException, Exception {
        if (!isLibrarianOrAdmin(currentUser)) {
            throw new AuthorizationException("Only ADMIN and LIBRARIAN can delete books");
        }

        Book book = bookDAO.findById(bookId);
        if (book == null) {
            throw new EntityNotFoundException("Book not found with ID: " + bookId);
        }

        return bookDAO.delete(bookId);
    }

    /**
     * Helper method to check if user is librarian or admin.
     */
    private boolean isLibrarianOrAdmin(User user) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        return user.getRole() == Role.LIBRARIAN || user.getRole() == Role.ADMIN;
    }
}

