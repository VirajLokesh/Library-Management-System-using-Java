package com.librarymanagement.util;

/**
 * Utility class to generate password hashes for initial database setup.
 * This is a helper class - not used in the main application.
 * 
 * Usage: Run this class's main method to generate password hashes.
 */
public class PasswordHashGenerator {
    public static void main(String[] args) {
        System.out.println("Password Hash Generator");
        System.out.println("=======================\n");
        
        String[] passwords = {"admin123", "librarian123", "member123"};
        
        for (String password : passwords) {
            String hash = PasswordUtils.hashPassword(password);
            System.out.println("Password: " + password);
            System.out.println("Hash: " + hash);
            System.out.println();
        }
    }
}

