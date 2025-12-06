package com.librarymanagement.model;

/**
 * Enum representing user roles in the library management system.
 * ADMIN: Full system access including user management
 * LIBRARIAN: Can manage books, members, and handle circulation
 * MEMBER: Can view books and manage their own loans
 */
public enum Role {
    ADMIN,
    LIBRARIAN,
    MEMBER
}

