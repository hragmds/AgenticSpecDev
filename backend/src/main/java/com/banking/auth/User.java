package com.banking.auth;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * User entity representing banking application users.
 * Contains authentication credentials and basic user information.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 255, message = "Password must be at least 6 characters")
    private String password;

    @Column(name = "full_name")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;

    @Column(name = "email")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Column(name = "account_number", unique = true)
    @Size(max = 20, message = "Account number cannot exceed 20 characters")
    private String accountNumber;

    @Column(name = "account_balance")
    private Double accountBalance = 0.0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Default constructor required by JPA
    public User() {
    }

    // Constructor for creating demo user
    public User(String username, String password, String fullName, String email, String accountNumber) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.accountNumber = accountNumber;
        this.accountBalance = 1000.0; // Demo starting balance
        this.isActive = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountBalance=" + accountBalance +
                ", isActive=" + isActive +
                '}';
    }
}