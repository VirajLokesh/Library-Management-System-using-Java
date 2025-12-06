package com.librarymanagement.ui;

import java.util.Scanner;

/**
 * Utility class for rendering menus and handling console input/output.
 */
public class MenuRenderer {
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Displays a menu with numbered options.
     *
     * @param title the menu title
     * @param options the menu options
     */
    public static void displayMenu(String title, String[] options) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(title);
        System.out.println("=".repeat(50));
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        System.out.println("=".repeat(50));
        System.out.print("Enter your choice: ");
    }

    /**
     * Reads an integer from the console.
     *
     * @param prompt the prompt message
     * @return the integer value
     */
    public static int readInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    /**
     * Reads a string from the console.
     *
     * @param prompt the prompt message
     * @return the string value
     */
    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Reads a string that cannot be empty.
     *
     * @param prompt the prompt message
     * @return the non-empty string value
     */
    public static String readNonEmptyString(String prompt) {
        while (true) {
            String input = readString(prompt);
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("This field cannot be empty. Please try again.");
        }
    }

    /**
     * Displays a message.
     *
     * @param message the message to display
     */
    public static void displayMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays an error message.
     *
     * @param message the error message
     */
    public static void displayError(String message) {
        System.out.println("ERROR: " + message);
    }

    /**
     * Displays a success message.
     *
     * @param message the success message
     */
    public static void displaySuccess(String message) {
        System.out.println("SUCCESS: " + message);
    }

    /**
     * Displays a table header.
     *
     * @param headers the column headers
     */
    public static void displayTableHeader(String[] headers) {
        System.out.println("\n" + "-".repeat(100));
        System.out.printf("%-5s", "");
        for (String header : headers) {
            System.out.printf("%-20s", header);
        }
        System.out.println();
        System.out.println("-".repeat(100));
    }

    /**
     * Pauses execution until user presses Enter.
     */
    public static void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Clears the console (platform-dependent).
     */
    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // If clearing fails, just print newlines
            System.out.println("\n\n\n\n\n");
        }
    }
}

