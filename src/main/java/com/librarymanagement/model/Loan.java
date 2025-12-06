package com.librarymanagement.model;

import java.time.LocalDate;

/**
 * Represents a loan transaction (book issue/return).
 * Tracks issue date, due date, return date, and fine amount.
 */
public class Loan {
    private Integer id;
    private Integer bookId;
    private Integer memberId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Double fineAmount;
    private LoanStatus status;

    /**
     * Enum for loan status
     */
    public enum LoanStatus {
        ISSUED,
        RETURNED,
        OVERDUE
    }

    // Default constructor
    public Loan() {
        this.fineAmount = 0.0;
        this.status = LoanStatus.ISSUED;
    }

    // Constructor for creating a new loan
    public Loan(Integer bookId, Integer memberId, LocalDate issueDate, LocalDate dueDate) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.fineAmount = 0.0;
        this.status = LoanStatus.ISSUED;
    }

    // Full constructor
    public Loan(Integer id, Integer bookId, Integer memberId, LocalDate issueDate,
                LocalDate dueDate, LocalDate returnDate, Double fineAmount, LoanStatus status) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
        this.status = status;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public Double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(Double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", memberId=" + memberId +
                ", issueDate=" + issueDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", fineAmount=" + fineAmount +
                ", status=" + status +
                '}';
    }
}

