package com.librarymanagement.ui;

import com.librarymanagement.config.DatabaseConfig;
import com.librarymanagement.exception.AuthenticationException;
import com.librarymanagement.model.Role;
import com.librarymanagement.model.User;
import com.librarymanagement.service.AuthService;

/**
 * Main entry point for the Library Management System console application.
 * Handles login and routes to appropriate menu based on user role.
 */
public class ConsoleApp {
    private final AuthService authService;

    public ConsoleApp() {
        this.authService = new AuthService();
    }

    /**
     * Main method to start the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        ConsoleApp app = new ConsoleApp();
        app.start();
    }

    /**
     * Starts the application and handles the main loop.
     */
    public void start() {
        displayWelcomeScreen();

        // Test database connection
        if (!DatabaseConfig.testConnection()) {
            MenuRenderer.displayError("Failed to connect to database. Please check your configuration.");
            MenuRenderer.displayMessage("Make sure MySQL is running and database.properties is configured correctly.");
            MenuRenderer.pause();
            return;
        }

        // Main application loop
        while (true) {
            try {
                User user = login();
                if (user != null) {
                    showRoleBasedMenu(user);
                }
            } catch (Exception e) {
                MenuRenderer.displayError("An error occurred: " + e.getMessage());
                MenuRenderer.pause();
            }
        }
    }

    /**
     * Displays the welcome screen.
     */
    private void displayWelcomeScreen() {
        MenuRenderer.clearScreen();
        System.out.println("\n" + "=".repeat(60));
        System.out.println("     WELCOME TO LIBRARY MANAGEMENT SYSTEM");
        System.out.println("=".repeat(60));
        System.out.println("\nPlease login to continue...\n");
    }

    /**
     * Handles user login.
     *
     * @return the authenticated User, or null if login is cancelled
     * @throws Exception if login fails
     */
    private User login() throws Exception {
        while (true) {
            String username = MenuRenderer.readString("Username (or 'exit' to quit): ");
            if (username.equalsIgnoreCase("exit")) {
                System.exit(0);
            }

            String password = MenuRenderer.readString("Password: ");

            try {
                User user = authService.login(username, password);
                MenuRenderer.displaySuccess("Login successful! Welcome, " + user.getUsername() + 
                    " (" + user.getRole() + ")");
                MenuRenderer.pause();
                return user;
            } catch (AuthenticationException e) {
                MenuRenderer.displayError(e.getMessage());
                String retry = MenuRenderer.readString("Try again? (y/n): ");
                if (!retry.equalsIgnoreCase("y")) {
                    return null;
                }
            }
        }
    }

    /**
     * Shows the appropriate menu based on user role.
     *
     * @param user the authenticated user
     */
    private void showRoleBasedMenu(User user) {
        Role role = user.getRole();

        try {
            if (role == Role.ADMIN) {
                AdminMenu adminMenu = new AdminMenu(user);
                adminMenu.showMenu();
            } else if (role == Role.LIBRARIAN) {
                LibrarianMenu librarianMenu = new LibrarianMenu(user);
                librarianMenu.showMenu();
            } else if (role == Role.MEMBER) {
                MemberMenu memberMenu = new MemberMenu(user);
                memberMenu.showMenu();
            } else {
                MenuRenderer.displayError("Unknown role: " + role);
            }
        } catch (Exception e) {
            MenuRenderer.displayError("Error in menu: " + e.getMessage());
            MenuRenderer.pause();
        }
    }
}

