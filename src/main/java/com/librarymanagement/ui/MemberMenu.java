package com.librarymanagement.ui;

import com.librarymanagement.dao.MemberDAO;
import com.librarymanagement.dao.MemberDAOImpl;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Loan;
import com.librarymanagement.model.Member;
import com.librarymanagement.model.User;
import com.librarymanagement.service.BookService;
import com.librarymanagement.service.LoanService;
import com.librarymanagement.util.DateUtils;

import java.util.List;

/**
 * Menu handler for MEMBER role.
 * Provides member functions (view books, search, view own loans).
 */
public class MemberMenu {
    private final User currentUser;
    private final BookService bookService;
    private final LoanService loanService;
    private final MemberDAO memberDAO;

    public MemberMenu(User currentUser) {
        this.currentUser = currentUser;
        this.bookService = new BookService();
        this.loanService = new LoanService();
        this.memberDAO = new MemberDAOImpl();
    }

    /**
     * Displays and handles the main member menu.
     */
    public void showMenu() {
        while (true) {
            String[] options = {
                "View All Books",
                "Search Books",
                "View My Active Loans",
                "View My Loan History",
                "Logout",
                "Exit"
            };
            MenuRenderer.displayMenu("MEMBER MENU", options);
            int choice = MenuRenderer.readInt("");

            try {
                switch (choice) {
                    case 1:
                        viewAllBooks();
                        break;
                    case 2:
                        searchBooks();
                        break;
                    case 3:
                        viewMyActiveLoans();
                        break;
                    case 4:
                        viewMyLoanHistory();
                        break;
                    case 5:
                        return; // Logout
                    case 6:
                        System.exit(0);
                    default:
                        MenuRenderer.displayError("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                MenuRenderer.displayError(e.getMessage());
                MenuRenderer.pause();
            }
        }
    }

    private void viewAllBooks() throws Exception {
        List<Book> books = bookService.getAllBooks();
        if (books.isEmpty()) {
            MenuRenderer.displayMessage("No books found.");
        } else {
            MenuRenderer.displayTableHeader(new String[]{"ID", "ISBN", "Title", "Author", "Available"});
            for (Book book : books) {
                System.out.printf("%-5d%-20s%-20s%-20s%-10d%n",
                    book.getId(), book.getIsbn(), book.getTitle(), 
                    book.getAuthor(), book.getAvailableCopies());
            }
        }
        MenuRenderer.pause();
    }

    private void searchBooks() throws Exception {
        MenuRenderer.displayMessage("Search by: title, author, or isbn");
        String searchType = MenuRenderer.readNonEmptyString("Enter search type: ");
        String searchTerm = MenuRenderer.readNonEmptyString("Enter search term: ");

        List<Book> books = bookService.searchBooks(searchTerm, searchType);
        if (books.isEmpty()) {
            MenuRenderer.displayMessage("No books found.");
        } else {
            MenuRenderer.displayTableHeader(new String[]{"ID", "ISBN", "Title", "Author", "Available"});
            for (Book book : books) {
                System.out.printf("%-5d%-20s%-20s%-20s%-10d%n",
                    book.getId(), book.getIsbn(), book.getTitle(), 
                    book.getAuthor(), book.getAvailableCopies());
            }
        }
        MenuRenderer.pause();
    }

    private void viewMyActiveLoans() throws Exception {
        Member member = memberDAO.findByUserId(currentUser.getId());
        if (member == null) {
            MenuRenderer.displayError("Member profile not found. Please contact administrator.");
            MenuRenderer.pause();
            return;
        }

        List<Loan> loans = loanService.getActiveLoansByMember(currentUser, member.getId());
        if (loans.isEmpty()) {
            MenuRenderer.displayMessage("You have no active loans.");
        } else {
            MenuRenderer.displayTableHeader(new String[]{"Loan ID", "Book ID", "Issue Date", "Due Date", "Status"});
            for (Loan loan : loans) {
                String status = DateUtils.isOverdue(loan.getDueDate(), loan.getReturnDate()) 
                    ? "OVERDUE" : loan.getStatus().name();
                System.out.printf("%-10d%-10d%-15s%-15s%-10s%n",
                    loan.getId(), loan.getBookId(),
                    DateUtils.formatDate(loan.getIssueDate()),
                    DateUtils.formatDate(loan.getDueDate()),
                    status);
            }
        }
        MenuRenderer.pause();
    }

    private void viewMyLoanHistory() throws Exception {
        Member member = memberDAO.findByUserId(currentUser.getId());
        if (member == null) {
            MenuRenderer.displayError("Member profile not found. Please contact administrator.");
            MenuRenderer.pause();
            return;
        }

        List<Loan> loans = loanService.getLoanHistoryByMember(currentUser, member.getId());
        if (loans.isEmpty()) {
            MenuRenderer.displayMessage("You have no loan history.");
        } else {
            MenuRenderer.displayTableHeader(new String[]{"Loan ID", "Book ID", "Issue Date", "Due Date", "Return Date", "Fine"});
            for (Loan loan : loans) {
                System.out.printf("%-10d%-10d%-15s%-15s%-15s%-10.2f%n",
                    loan.getId(), loan.getBookId(),
                    DateUtils.formatDate(loan.getIssueDate()),
                    DateUtils.formatDate(loan.getDueDate()),
                    loan.getReturnDate() != null ? DateUtils.formatDate(loan.getReturnDate()) : "Not returned",
                    loan.getFineAmount());
            }
        }
        MenuRenderer.pause();
    }
}

