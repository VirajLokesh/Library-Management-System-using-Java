package com.librarymanagement.dao;

import com.librarymanagement.model.Loan;
import java.util.List;

public interface LoanDAO extends BaseDAO<Loan, Integer> {
    List<Loan> findActiveLoansByMember(Integer memberId) throws Exception;

    List<Loan> findAllLoansByMember(Integer memberId) throws Exception;

    Loan findActiveLoanByBookAndMember(Integer bookId, Integer memberId) throws Exception;

    List<Loan> findOverdueLoans() throws Exception;

    List<Loan> findAllActiveLoans() throws Exception;

    int countActiveLoansByMember(Integer memberId) throws Exception;
}
