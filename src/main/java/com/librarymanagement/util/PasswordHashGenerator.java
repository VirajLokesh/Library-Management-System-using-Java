package com.librarymanagement.util;

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
