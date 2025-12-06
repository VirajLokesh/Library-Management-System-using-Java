package com.librarymanagement.service;

import com.librarymanagement.dao.BookDAO;
import com.librarymanagement.dao.BookDAOImpl;
import com.librarymanagement.dao.LoanDAO;
import com.librarymanagement.dao.LoanDAOImpl;
import com.librarymanagement.dao.MemberDAO;
import com.librarymanagement.dao.MemberDAOImpl;
import com.librarymanagement.exception.AuthorizationException;
import com.librarymanagement.exception.EntityNotFoundException;
import com.librarymanagement.exception.ValidationException;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Loan;
import com.librarymanagement.model.Member;
import com.librarymanagement.model.Role;
import com.librarymanagement.model.User;
import com.librarymanagement.util.DateUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class for loan (circulation) operations.
 * Handles business logic for issuing and returning books.
 */
public class LoanService {
    private final LoanDAO loanDAO;
    private final BookDAO bookDAO;
    private final MemberDAO memberDAO;

    public LoanService() {
        this.loanDAO = new LoanDAOImpl();
        this.bookDAO = new BookDAOImpl();
        this.memberDAO = new MemberDAOImpl();
    }

    /**
     * Constructor for dependency injection.
     *
     * @param loanDAO the LoanDAO implementation
     * @param bookDAO the BookDAO implementation
     * @param memberDAO the MemberDAO implementation
     */
    public LoanService(LoanDAO loanDAO, BookDAO bookDAO, MemberDAO memberDAO) {
        this.loanDAO = loanDAO;
        this.bookDAO = bookDAO;
        this.memberDAO = memberDAO;
    }

    /**
     * Issues a book to a member. Only ADMIN and LIBRARIAN can issue books.
     *
     * @param currentUser the user performing the operation
     * @param bookId the book ID
     * @param memberId the member ID
     * @return the created Loan
     * @throws AuthorizationException if current user is not authorized
     * @throws EntityNotFoundException if book or member not found
     * @throws ValidationException if validation fails
     * @throws Exception if operation fails
     */
    public Loan issueBook(User currentUser, Integer bookId, Integer memberId) 
            throws AuthorizationException, EntityNotFoundException, ValidationException, Exception {
        // Authorization check
        if (!isLibrarianOrAdmin(currentUser)) {
            throw new AuthorizationException("Only ADMIN and LIBRARIAN can issue books");
        }

        // Validate book exists and is available
        Book book = bookDAO.findById(bookId);
        if (book == null) {
            throw new EntityNotFoundException("Book not found with ID: " + bookId);
        }

        if (book.getAvailableCopies() <= 0) {
            throw new ValidationException("No copies available for this book");
        }

        // Validate member exists
        Member member = memberDAO.findById(memberId);
        if (member == null) {
            throw new EntityNotFoundException("Member not found with ID: " + memberId);
        }

        // Check if member has exceeded max active loans
        int activeLoanCount = loanDAO.countActiveLoansByMember(memberId);
        if (activeLoanCount >= DateUtils.MAX_ACTIVE_LOANS) {
            throw new ValidationException("Member has reached maximum active loans (" + 
                                        DateUtils.MAX_ACTIVE_LOANS + ")");
        }

        // Create loan
        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = DateUtils.calculateDueDate(issueDate);
        Loan loan = new Loan(bookId, memberId, issueDate, dueDate);
        loan = loanDAO.create(loan);

        // Update book available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookDAO.update(book);

        return loan;
    }

    /**
     * Returns a book. Only ADMIN and LIBRARIAN can return books.
     *
     * @param currentUser the user performing the operation
     * @param bookId the book ID
     * @param memberId the member ID
     * @return the updated Loan
     * @throws AuthorizationException if current user is not authorized
     * @throws EntityNotFoundException if loan not found
     * @throws ValidationException if validation fails
     * @throws Exception if operation fails
     */
    public Loan returnBook(User currentUser, Integer bookId, Integer memberId) 
            throws AuthorizationException, EntityNotFoundException, ValidationException, Exception {
        // Authorization check
        if (!isLibrarianOrAdmin(currentUser)) {
            throw new AuthorizationException("Only ADMIN and LIBRARIAN can return books");
        }

        // Find active loan
        Loan loan = loanDAO.findActiveLoanByBookAndMember(bookId, memberId);
        if (loan == null) {
            throw new EntityNotFoundException("No active loan found for this book and member");
        }

        // Calculate fine if overdue
        LocalDate returnDate = LocalDate.now();
        double fine = DateUtils.calculateFine(loan.getDueDate(), returnDate);
        
        // Update loan
        loan.setReturnDate(returnDate);
        loan.setFineAmount(fine);
        loan.setStatus(Loan.LoanStatus.RETURNED);
        loanDAO.update(loan);

        // Update book available copies
        Book book = bookDAO.findById(bookId);
        if (book != null) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookDAO.update(book);
        }

        return loan;
    }

    /**
     * Gets all overdue loans. Only ADMIN and LIBRARIAN can view overdue loans.
     *
     * @param currentUser the user performing the operation
     * @return list of overdue loans
     * @throws AuthorizationException if current user is not authorized
     * @throws Exception if query fails
     */
    public List<Loan> getOverdueLoans(User currentUser) throws AuthorizationException, Exception {
        if (!isLibrarianOrAdmin(currentUser)) {
            throw new AuthorizationException("Only ADMIN and LIBRARIAN can view overdue loans");
        }

        List<Loan> loans = loanDAO.findOverdueLoans();
        
        // Update status for any loans that are overdue but not marked as such
        LocalDate today = LocalDate.now();
        for (Loan loan : loans) {
            if (loan.getStatus() != Loan.LoanStatus.OVERDUE && 
                loan.getReturnDate() == null && 
                today.isAfter(loan.getDueDate())) {
                loan.setStatus(Loan.LoanStatus.OVERDUE);
                loanDAO.update(loan);
            }
        }
        
        return loans;
    }

    /**
     * Gets all currently issued books. Only ADMIN and LIBRARIAN can view all issued books.
     *
     * @param currentUser the user performing the operation
     * @return list of active loans
     * @throws AuthorizationException if current user is not authorized
     * @throws Exception if query fails
     */
    public List<Loan> getAllActiveLoans(User currentUser) throws AuthorizationException, Exception {
        if (!isLibrarianOrAdmin(currentUser)) {
            throw new AuthorizationException("Only ADMIN and LIBRARIAN can view all active loans");
        }
        return loanDAO.findAllActiveLoans();
    }

    /**
     * Gets loan history for a member. Members can only view their own history.
     *
     * @param currentUser the user performing the operation
     * @param memberId the member ID
     * @return list of all loans for the member
     * @throws AuthorizationException if current user is not authorized
     * @throws EntityNotFoundException if member not found
     * @throws Exception if query fails
     */
    public List<Loan> getLoanHistoryByMember(User currentUser, Integer memberId) 
            throws AuthorizationException, EntityNotFoundException, Exception {
        // Check if member exists
        Member member = memberDAO.findById(memberId);
        if (member == null) {
            throw new EntityNotFoundException("Member not found with ID: " + memberId);
        }

        // Members can only view their own history
        if (currentUser.getRole() == Role.MEMBER) {
            Member currentMember = memberDAO.findByUserId(currentUser.getId());
            if (currentMember == null || !currentMember.getId().equals(memberId)) {
                throw new AuthorizationException("Members can only view their own loan history");
            }
        } else if (!isLibrarianOrAdmin(currentUser)) {
            throw new AuthorizationException("Unauthorized access");
        }

        return loanDAO.findAllLoansByMember(memberId);
    }

    /**
     * Gets active loans for a member. Members can only view their own active loans.
     *
     * @param currentUser the user performing the operation
     * @param memberId the member ID
     * @return list of active loans for the member
     * @throws AuthorizationException if current user is not authorized
     * @throws EntityNotFoundException if member not found
     * @throws Exception if query fails
     */
    public List<Loan> getActiveLoansByMember(User currentUser, Integer memberId) 
            throws AuthorizationException, EntityNotFoundException, Exception {
        // Check if member exists
        Member member = memberDAO.findById(memberId);
        if (member == null) {
            throw new EntityNotFoundException("Member not found with ID: " + memberId);
        }

        // Members can only view their own active loans
        if (currentUser.getRole() == Role.MEMBER) {
            Member currentMember = memberDAO.findByUserId(currentUser.getId());
            if (currentMember == null || !currentMember.getId().equals(memberId)) {
                throw new AuthorizationException("Members can only view their own active loans");
            }
        } else if (!isLibrarianOrAdmin(currentUser)) {
            throw new AuthorizationException("Unauthorized access");
        }

        return loanDAO.findActiveLoansByMember(memberId);
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

