package com.banking.accounts;

import com.banking.transactions.Transaction;
import com.banking.transactions.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * T045 - TransactionService implementation for banking application.
 * 
 * Provides business logic for transaction operations including money transfers.
 * Implements transaction history, transfer execution, and validation per FR-006, FR-007, FR-009.
 */
@Service
@Transactional
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountService accountService;

    /**
     * Get recent transactions for a user across all their accounts.
     * Used by dashboard to show transaction history (FR-006).
     * 
     * @param userId The user ID
     * @param limit Maximum number of transactions to return (default 10)
     * @return List of recent transactions ordered by date descending
     */
    @Transactional(readOnly = true)
    public List<Transaction> getRecentTransactions(Long userId, int limit) {
        logger.info("Retrieving {} recent transactions for user: {}", limit, userId);
        
        // First get user's accounts
        List<Account> userAccounts = accountService.getAccountsByUserId(userId);
        List<Long> accountIds = userAccounts.stream().map(Account::getId).toList();
        
        if (accountIds.isEmpty()) {
            logger.info("No accounts found for user: {}", userId);
            return List.of();
        }
        
        Pageable pageable = PageRequest.of(0, limit);
        List<Transaction> transactions = transactionRepository.findRecentTransactionsByAccountIds(accountIds, pageable);
        
        logger.info("Found {} recent transactions for user: {}", transactions.size(), userId);
        return transactions;
    }

    /**
     * Get recent transactions for a user with default limit of 10.
     * 
     * @param userId The user ID
     * @return List of up to 10 recent transactions
     */
    @Transactional(readOnly = true)
    public List<Transaction> getRecentTransactions(Long userId) {
        return getRecentTransactions(userId, 10);
    }

    /**
     * Get transactions for a specific account.
     * Used by account detail view to show account-specific history.
     * 
     * @param accountId The account ID
     * @param userId The user ID (for security validation)
     * @param limit Maximum number of transactions to return
     * @return List of transactions for the account
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByAccount(Long accountId, Long userId, int limit) {
        logger.info("Retrieving {} transactions for account {} (user: {})", limit, accountId, userId);
        
        // Validate account belongs to user
        accountService.getAccountById(accountId, userId);
        
        Pageable pageable = PageRequest.of(0, limit);
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId, pageable);
        
        logger.info("Found {} transactions for account: {}", transactions.size(), accountId);
        return transactions;
    }

    /**
     * Execute a money transfer between accounts (FR-007, FR-009).
     * Validates funds availability and performs atomic transfer.
     * Creates transaction records for both accounts.
     * 
     * @param fromAccountId Source account ID
     * @param toAccountId Destination account ID
     * @param amount Transfer amount
     * @param description Transfer description
     * @param userId User ID (must own source account)
     * @return The created transaction record
     * @throws IllegalArgumentException if validation fails
     * @throws IllegalStateException if transfer cannot be completed
     */
    public Transaction executeTransfer(Long fromAccountId, Long toAccountId, BigDecimal amount, 
                                     String description, Long userId) {
        logger.info("Executing transfer: from={}, to={}, amount={}, user={}", 
                   fromAccountId, toAccountId, amount, userId);
        
        // Validation
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        
        // Validate source account belongs to user and has sufficient funds
        Account fromAccount = accountService.getAccountById(fromAccountId, userId);
        if (!accountService.hasSufficientBalance(fromAccountId, amount, userId)) {
            logger.error("Insufficient funds for transfer: available={}, required={}", 
                        fromAccount.getBalance(), amount);
            throw new IllegalArgumentException("Insufficient funds for transfer");
        }
        
        // Validate destination account exists (don't require same user ownership for transfers)
        try {
            // For now, we'll validate the destination account exists by trying to get it
            // In a real system, we might have cross-user transfer validation
            accountService.getAccountById(toAccountId, userId);
        } catch (IllegalArgumentException e) {
            logger.error("Destination account {} not found or inaccessible", toAccountId);
            throw new IllegalArgumentException("Invalid destination account");
        }
        
        try {
            // Execute transfer atomically
            BigDecimal newFromBalance = fromAccount.getBalance().subtract(amount);
            accountService.updateBalance(fromAccountId, newFromBalance, userId);
            
            // For simplicity, we'll assume destination account belongs to same user
            // In real system, this would require more complex authorization
            Account toAccount = accountService.getAccountById(toAccountId, userId);
            BigDecimal newToBalance = toAccount.getBalance().add(amount);
            accountService.updateBalance(toAccountId, newToBalance, userId);
            
            // Create transaction record
            Transaction transaction = new Transaction(fromAccountId, toAccountId, amount, description);
            Transaction savedTransaction = transactionRepository.save(transaction);
            
            logger.info("Transfer completed successfully: transaction ID={}", savedTransaction.getId());
            return savedTransaction;
            
        } catch (Exception e) {
            logger.error("Transfer failed: {}", e.getMessage());
            throw new IllegalStateException("Transfer could not be completed: " + e.getMessage());
        }
    }

    /**
     * Record a deposit transaction (for demo/testing purposes).
     * 
     * @param accountId The account ID
     * @param amount Deposit amount
     * @param description Deposit description
     * @param userId User ID
     * @return The created transaction record
     */
    public Transaction recordDeposit(Long accountId, BigDecimal amount, String description, Long userId) {
        logger.info("Recording deposit: account={}, amount={}, user={}", accountId, amount, userId);
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        
        Account account = accountService.getAccountById(accountId, userId);
        BigDecimal newBalance = account.getBalance().add(amount);
        accountService.updateBalance(accountId, newBalance, userId);
        
        // Create transaction record (no source account for deposits)
        Transaction transaction = new Transaction(null, accountId, amount, description);
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        logger.info("Deposit recorded successfully: transaction ID={}", savedTransaction.getId());
        return savedTransaction;
    }

    /**
     * Record a withdrawal transaction (for demo/testing purposes).
     * 
     * @param accountId The account ID
     * @param amount Withdrawal amount
     * @param description Withdrawal description
     * @param userId User ID
     * @return The created transaction record
     */
    public Transaction recordWithdrawal(Long accountId, BigDecimal amount, String description, Long userId) {
        logger.info("Recording withdrawal: account={}, amount={}, user={}", accountId, amount, userId);
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        if (!accountService.hasSufficientBalance(accountId, amount, userId)) {
            throw new IllegalArgumentException("Insufficient funds for withdrawal");
        }
        
        Account account = accountService.getAccountById(accountId, userId);
        BigDecimal newBalance = account.getBalance().subtract(amount);
        accountService.updateBalance(accountId, newBalance, userId);
        
        // Create transaction record (no destination account for withdrawals)
        Transaction transaction = new Transaction(accountId, null, amount, description);
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        logger.info("Withdrawal recorded successfully: transaction ID={}", savedTransaction.getId());
        return savedTransaction;
    }

    /**
     * Get transaction count for a user.
     * Used for analytics and pagination.
     * 
     * @param userId The user ID
     * @return Total number of transactions for the user
     */
    @Transactional(readOnly = true)
    public long getTransactionCount(Long userId) {
        logger.info("Counting transactions for user: {}", userId);
        
        // Get all user's accounts first
        List<Account> userAccounts = accountService.getAccountsByUserId(userId);
        
        long totalCount = 0;
        for (Account account : userAccounts) {
            totalCount += transactionRepository.countByAccountId(account.getId());
        }
        
        logger.info("User {} has {} transactions", userId, totalCount);
        return totalCount;
    }
}