package com.banking.accounts;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * T040 - Account entity for banking application.
 * 
 * Represents a financial account belonging to a user with balance and type information.
 * Per data-model.md specifications and FR-014, FR-015.
 */
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "account_type", nullable = false, length = 20)
    @Pattern(regexp = "CHECKING|SAVINGS", message = "Account type must be CHECKING or SAVINGS")
    private String accountType;

    @Column(name = "account_name", nullable = false, length = 100)
    @NotBlank(message = "Account name is required")
    @Size(max = 100, message = "Account name cannot exceed 100 characters")
    private String accountName;

    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    @DecimalMin(value = "0.00", message = "Balance cannot be negative")
    @Digits(integer = 13, fraction = 2, message = "Balance must have at most 2 decimal places")
    private BigDecimal balance;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Default constructor
    public Account() {}

    // Constructor with parameters
    public Account(Long userId, String accountType, String accountName, BigDecimal balance) {
        this.userId = userId;
        this.accountType = accountType;
        this.accountName = accountName;
        this.balance = balance;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", userId=" + userId +
                ", accountType='" + accountType + '\'' +
                ", accountName='" + accountName + '\'' +
                ", balance=" + balance +
                ", createdAt=" + createdAt +
                '}';
    }
}