package com.librarymanagement.ui;

import com.librarymanagement.model.Book;
import com.librarymanagement.model.Loan;
import com.librarymanagement.model.Member;
import com.librarymanagement.model.Role;
import com.librarymanagement.model.User;
import com.librarymanagement.service.BookService;
import com.librarymanagement.service.LoanService;
import com.librarymanagement.service.MemberService;
import com.librarymanagement.service.UserService;
import com.librarymanagement.util.DateUtils;

import java.util.List;

/**
 * Menu handler for ADMIN role.
 * Provides all administrative functions.
 */
public class AdminMenu {
    private final User currentUser;
    private final UserService userService;
    private final BookService bookService;
    private final MemberService memberService;
    private final LoanService loanService;

    public AdminMenu(User currentUser) {
        this.currentUser = currentUser;
        this.userService = new UserService();
        this.bookService = new BookService();
        this.memberService = new MemberService();
        this.loanService = new LoanService();
    }

    /**
     * Displays and handles the main admin menu.
     */
    public void showMenu() {
        while (true) {
            String[] options = {
                "Manage Users",
                "Manage Books",
                "Manage Members",
                "View Reports",
                "Logout",
                "Exit"
            };
            MenuRenderer.displayMenu("ADMIN MENU", options);
            int choice = MenuRenderer.readInt("");

            try {
                switch (choice) {
                    case 1:
                        manageUsers();
                        break;
                    case 2:
                        manageBooks();
                        break;
                    case 3:
                        manageMembers();
                        break;
                    case 4:
                        viewReports();
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

    private void manageUsers() throws Exception {
        while (true) {
            String[] options = {
                "List Users",
                "Create User",
                "Update User",
                "Delete User",
                "Back"
            };
            MenuRenderer.displayMenu("USER MANAGEMENT", options);
            int choice = MenuRenderer.readInt("");

            switch (choice) {
                case 1:
                    listUsers();
                    break;
                case 2:
                    createUser();
                    break;
                case 3:
                    updateUser();
                    break;
                case 4:
                    deleteUser();
                    break;
                case 5:
                    return;
                default:
                    MenuRenderer.displayError("Invalid choice.");
            }
        }
    }

    private void listUsers() {
        MenuRenderer.displayMessage("\nUser listing feature - To be implemented with UserDAO.findAll()");
        MenuRenderer.pause();
    }

    private void createUser() throws Exception {
        String username = MenuRenderer.readNonEmptyString("Enter username: ");
        String password = MenuRenderer.readNonEmptyString("Enter password: ");
        
        MenuRenderer.displayMessage("Roles: ADMIN, LIBRARIAN, MEMBER");
        String roleStr = MenuRenderer.readNonEmptyString("Enter role: ");
        Role role = Role.valueOf(roleStr.toUpperCase());

        User user = userService.createUser(currentUser, username, password, role);
        MenuRenderer.displaySuccess("User created successfully with ID: " + user.getId());
        MenuRenderer.pause();
    }

    private void updateUser() throws Exception {
        int userId = MenuRenderer.readInt("Enter user ID to update: ");
        String username = MenuRenderer.readString("Enter new username (press Enter to skip): ");
        String password = MenuRenderer.readString("Enter new password (press Enter to skip): ");
        String roleStr = MenuRenderer.readString("Enter new role (ADMIN/LIBRARIAN/MEMBER, press Enter to skip): ");

        Role role = null;
        if (!roleStr.isEmpty()) {
            role = Role.valueOf(roleStr.toUpperCase());
        }

        boolean updated = userService.updateUser(currentUser, userId, 
            username.isEmpty() ? null : username,
            password.isEmpty() ? null : password,
            role);
        
        if (updated) {
            MenuRenderer.displaySuccess("User updated successfully");
        } else {
            MenuRenderer.displayError("Failed to update user");
        }
        MenuRenderer.pause();
    }

    private void deleteUser() throws Exception {
        int userId = MenuRenderer.readInt("Enter user ID to delete: ");
        boolean deleted = userService.deleteUser(currentUser, userId);
        if (deleted) {
            MenuRenderer.displaySuccess("User deleted successfully");
        } else {
            MenuRenderer.displayError("Failed to delete user");
        }
        MenuRenderer.pause();
    }

    private void manageBooks() throws Exception {
        while (true) {
            String[] options = {
                "List All Books",
                "Search Books",
                "Create Book",
                "Update Book",
                "Delete Book",
                "Back"
            };
            MenuRenderer.displayMenu("BOOK MANAGEMENT", options);
            int choice = MenuRenderer.readInt("");

            switch (choice) {
                case 1:
                    listAllBooks();
                    break;
                case 2:
                    searchBooks();
                    break;
                case 3:
                    createBook();
                    break;
                case 4:
                    updateBook();
                    break;
                case 5:
                    deleteBook();
                    break;
                case 6:
                    return;
                default:
                    MenuRenderer.displayError("Invalid choice.");
            }
        }
    }

    private void listAllBooks() throws Exception {
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

    private void createBook() throws Exception {
        String isbn = MenuRenderer.readNonEmptyString("Enter ISBN: ");
        String title = MenuRenderer.readNonEmptyString("Enter title: ");
        String author = MenuRenderer.readNonEmptyString("Enter author: ");
        String publisher = MenuRenderer.readString("Enter publisher (optional): ");
        int publishedYear = MenuRenderer.readInt("Enter published year: ");
        int totalCopies = MenuRenderer.readInt("Enter total copies: ");

        Book book = bookService.createBook(currentUser, isbn, title, author, 
            publisher.isEmpty() ? null : publisher, publishedYear, totalCopies);
        MenuRenderer.displaySuccess("Book created successfully with ID: " + book.getId());
        MenuRenderer.pause();
    }

    private void updateBook() throws Exception {
        int bookId = MenuRenderer.readInt("Enter book ID to update: ");
        String isbn = MenuRenderer.readString("Enter new ISBN (press Enter to skip): ");
        String title = MenuRenderer.readString("Enter new title (press Enter to skip): ");
        String author = MenuRenderer.readString("Enter new author (press Enter to skip): ");
        String publisher = MenuRenderer.readString("Enter new publisher (press Enter to skip): ");
        String yearStr = MenuRenderer.readString("Enter new published year (press Enter to skip): ");
        String copiesStr = MenuRenderer.readString("Enter new total copies (press Enter to skip): ");

        Integer publishedYear = yearStr.isEmpty() ? null : Integer.parseInt(yearStr);
        Integer totalCopies = copiesStr.isEmpty() ? null : Integer.parseInt(copiesStr);

        boolean updated = bookService.updateBook(currentUser, bookId,
            isbn.isEmpty() ? null : isbn,
            title.isEmpty() ? null : title,
            author.isEmpty() ? null : author,
            publisher.isEmpty() ? null : publisher,
            publishedYear, totalCopies);

        if (updated) {
            MenuRenderer.displaySuccess("Book updated successfully");
        } else {
            MenuRenderer.displayError("Failed to update book");
        }
        MenuRenderer.pause();
    }

    private void deleteBook() throws Exception {
        int bookId = MenuRenderer.readInt("Enter book ID to delete: ");
        boolean deleted = bookService.deleteBook(currentUser, bookId);
        if (deleted) {
            MenuRenderer.displaySuccess("Book deleted successfully");
        } else {
            MenuRenderer.displayError("Failed to delete book");
        }
        MenuRenderer.pause();
    }

    private void manageMembers() throws Exception {
        while (true) {
            String[] options = {
                "List All Members",
                "Create Member",
                "Update Member",
                "Delete Member",
                "Back"
            };
            MenuRenderer.displayMenu("MEMBER MANAGEMENT", options);
            int choice = MenuRenderer.readInt("");

            switch (choice) {
                case 1:
                    listAllMembers();
                    break;
                case 2:
                    createMember();
                    break;
                case 3:
                    updateMember();
                    break;
                case 4:
                    deleteMember();
                    break;
                case 5:
                    return;
                default:
                    MenuRenderer.displayError("Invalid choice.");
            }
        }
    }

    private void listAllMembers() throws Exception {
        List<Member> members = memberService.getAllMembers(currentUser);
        if (members.isEmpty()) {
            MenuRenderer.displayMessage("No members found.");
        } else {
            MenuRenderer.displayTableHeader(new String[]{"ID", "Name", "Email", "Phone"});
            for (Member member : members) {
                System.out.printf("%-5d%-20s%-30s%-15s%n",
                    member.getId(), member.getName(), 
                    member.getEmail() != null ? member.getEmail() : "",
                    member.getPhone() != null ? member.getPhone() : "");
            }
        }
        MenuRenderer.pause();
    }

    private void createMember() throws Exception {
        int userId = MenuRenderer.readInt("Enter user ID (must be MEMBER role): ");
        String name = MenuRenderer.readNonEmptyString("Enter member name: ");
        String email = MenuRenderer.readString("Enter email (optional): ");
        String phone = MenuRenderer.readString("Enter phone (optional): ");

        Member member = memberService.createMember(currentUser, userId, name,
            email.isEmpty() ? null : email,
            phone.isEmpty() ? null : phone);
        MenuRenderer.displaySuccess("Member created successfully with ID: " + member.getId());
        MenuRenderer.pause();
    }

    private void updateMember() throws Exception {
        int memberId = MenuRenderer.readInt("Enter member ID to update: ");
        String name = MenuRenderer.readString("Enter new name (press Enter to skip): ");
        String email = MenuRenderer.readString("Enter new email (press Enter to skip): ");
        String phone = MenuRenderer.readString("Enter new phone (press Enter to skip): ");

        boolean updated = memberService.updateMember(currentUser, memberId,
            name.isEmpty() ? null : name,
            email.isEmpty() ? null : email,
            phone.isEmpty() ? null : phone);

        if (updated) {
            MenuRenderer.displaySuccess("Member updated successfully");
        } else {
            MenuRenderer.displayError("Failed to update member");
        }
        MenuRenderer.pause();
    }

    private void deleteMember() throws Exception {
        int memberId = MenuRenderer.readInt("Enter member ID to delete: ");
        boolean deleted = memberService.deleteMember(currentUser, memberId);
        if (deleted) {
            MenuRenderer.displaySuccess("Member deleted successfully");
        } else {
            MenuRenderer.displayError("Failed to delete member");
        }
        MenuRenderer.pause();
    }

    private void viewReports() throws Exception {
        while (true) {
            String[] options = {
                "View Overdue Books",
                "View All Issued Books",
                "View Loan History for Member",
                "Back"
            };
            MenuRenderer.displayMenu("REPORTS", options);
            int choice = MenuRenderer.readInt("");

            switch (choice) {
                case 1:
                    viewOverdueBooks();
                    break;
                case 2:
                    viewAllIssuedBooks();
                    break;
                case 3:
                    viewLoanHistory();
                    break;
                case 4:
                    return;
                default:
                    MenuRenderer.displayError("Invalid choice.");
            }
        }
    }

    private void viewOverdueBooks() throws Exception {
        List<Loan> loans = loanService.getOverdueLoans(currentUser);
        if (loans.isEmpty()) {
            MenuRenderer.displayMessage("No overdue books.");
        } else {
            MenuRenderer.displayTableHeader(new String[]{"Loan ID", "Book ID", "Member ID", "Due Date", "Fine"});
            for (Loan loan : loans) {
                System.out.printf("%-10d%-10d%-12d%-15s%-10.2f%n",
                    loan.getId(), loan.getBookId(), loan.getMemberId(),
                    DateUtils.formatDate(loan.getDueDate()), loan.getFineAmount());
            }
        }
        MenuRenderer.pause();
    }

    private void viewAllIssuedBooks() throws Exception {
        List<Loan> loans = loanService.getAllActiveLoans(currentUser);
        if (loans.isEmpty()) {
            MenuRenderer.displayMessage("No active loans.");
        } else {
            MenuRenderer.displayTableHeader(new String[]{"Loan ID", "Book ID", "Member ID", "Issue Date", "Due Date"});
            for (Loan loan : loans) {
                System.out.printf("%-10d%-10d%-12d%-15s%-15s%n",
                    loan.getId(), loan.getBookId(), loan.getMemberId(),
                    DateUtils.formatDate(loan.getIssueDate()),
                    DateUtils.formatDate(loan.getDueDate()));
            }
        }
        MenuRenderer.pause();
    }

    private void viewLoanHistory() throws Exception {
        int memberId = MenuRenderer.readInt("Enter member ID: ");
        List<Loan> loans = loanService.getLoanHistoryByMember(currentUser, memberId);
        if (loans.isEmpty()) {
            MenuRenderer.displayMessage("No loan history found.");
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

