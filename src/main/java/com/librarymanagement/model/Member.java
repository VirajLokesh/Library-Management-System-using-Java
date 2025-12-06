package com.librarymanagement.model;

import java.time.LocalDate;

/**
 * Represents a library member (student/user).
 * Each member is associated with a User account.
 */
public class Member {
    private Integer id;
    private Integer userId;
    private String name;
    private String email;
    private String phone;
    private LocalDate joinDate;

    // Default constructor
    public Member() {
    }

    // Constructor without id (for creation)
    public Member(Integer userId, String name, String email, String phone) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Full constructor
    public Member(Integer id, Integer userId, String name, String email, 
                  String phone, LocalDate joinDate) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.joinDate = joinDate;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", joinDate=" + joinDate +
                '}';
    }
}

