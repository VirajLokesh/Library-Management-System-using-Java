package com.librarymanagement.dao;

import com.librarymanagement.model.Loan;
import java.util.List;

/**
 * Data Access Object interface for Loan entity.
 * Extends BaseDAO and adds loan-specific methods.
 */
public interface LoanDAO extends BaseDAO<Loan, Integer> {
    /**
     * Finds all active loans (not returned) for a member.
     *
     * @param memberId the member ID
     * @return list of active loans
     * @throws Exception if query fails
     */
    List<Loan> findActiveLoansByMember(Integer memberId) throws Exception;

    /**
     * Finds all loans (including returned) for a member.
     *
     * @param memberId the member ID
     * @return list of all loans
     * @throws Exception if query fails
     */
    List<Loan> findAllLoansByMember(Integer memberId) throws Exception;

    /**
     * Finds the active loan for a specific book and member.
     *
     * @param bookId the book ID
     * @param memberId the member ID
     * @return the Loan if found, null otherwise
     * @throws Exception if query fails
     */
    Loan findActiveLoanByBookAndMember(Integer bookId, Integer memberId) throws Exception;

    /**
     * Finds all overdue loans.
     *
     * @return list of overdue loans
     * @throws Exception if query fails
     */
    List<Loan> findOverdueLoans() throws Exception;

    /**
     * Finds all currently issued books (active loans).
     *
     * @return list of active loans
     * @throws Exception if query fails
     */
    List<Loan> findAllActiveLoans() throws Exception;

    /**
     * Counts active loans for a member.
     *
     * @param memberId the member ID
     * @return count of active loans
     * @throws Exception if query fails
     */
    int countActiveLoansByMember(Integer memberId) throws Exception;
}

