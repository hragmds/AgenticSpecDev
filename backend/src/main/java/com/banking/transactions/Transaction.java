package com.banking.transactions;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * T041 - Transaction entity for banking application.
 * 
 * Represents a money transfer between accounts with timestamp and audit information.
 * Per data-model.md specifications and FR-011, FR-012.
 */
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_account_id")
    private Long fromAccountId; // Nullable for future deposits/withdrawals

    @Column(name = "to_account_id")
    private Long toAccountId; // Nullable for withdrawals

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    @DecimalMin(value = "0.01", message = "Transaction amount must be positive")
    @Digits(integer = 13, fraction = 2, message = "Amount must have at most 2 decimal places")
    private BigDecimal amount;

    @Column(name = "description", length = 255)
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    // Default constructor
    public Transaction() {}

    // Constructor with parameters
    public Transaction(Long fromAccountId, Long toAccountId, BigDecimal amount, String description) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.description = description;
        this.transactionDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    @PrePersist
    protected void onCreate() {
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
        }
    }

    /**
     * Validation: fromAccountId and toAccountId cannot be identical (per FR-012)
     */
    @AssertTrue(message = "Source and destination accounts cannot be the same")
    public boolean isValidAccountTransfer() {
        if (fromAccountId == null || toAccountId == null) {
            return true; // Allow null fromAccountId for deposits
        }
        return !fromAccountId.equals(toAccountId);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", fromAccountId=" + fromAccountId +
                ", toAccountId=" + toAccountId +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", transactionDate=" + transactionDate +
                '}';
    }
}