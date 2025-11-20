package com.banking.accounts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * T044 - AccountService implementation for banking application.
 * 
 * Provides business logic for account operations including balance management.
 * Implements account retrieval, balance updates, and validation per business rules.
 */
@Service
@Transactional
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountRepository accountRepository;

    /**
     * Get all accounts for a specific user.
     * Used by dashboard to display user's accounts with balances.
     * 
     * @param userId The user ID
     * @return List of user's accounts ordered by creation date
     */
    @Transactional(readOnly = true)
    public List<Account> getAccountsByUserId(Long userId) {
        logger.info("Retrieving accounts for user: {}", userId);
        
        List<Account> accounts = accountRepository.findByUserIdOrderByCreatedAtAsc(userId);
        
        logger.info("Found {} accounts for user: {}", accounts.size(), userId);
        return accounts;
    }

    /**
     * Get a specific account by ID for a user.
     * Ensures users can only access their own accounts (security).
     * 
     * @param accountId The account ID
     * @param userId The user ID
     * @return The account if found and belongs to user
     * @throws IllegalArgumentException if account not found or doesn't belong to user
     */
    @Transactional(readOnly = true)
    public Account getAccountById(Long accountId, Long userId) {
        logger.info("Retrieving account {} for user: {}", accountId, userId);
        
        return accountRepository.findByIdAndUserId(accountId, userId)
            .orElseThrow(() -> {
                logger.error("Account {} not found or doesn't belong to user: {}", accountId, userId);
                return new IllegalArgumentException("Account not found or access denied");
            });
    }

    /**
     * Update account balance.
     * Used during money transfers to adjust account balances.
     * Validates that balance doesn't go negative (per FR-009).
     * 
     * @param accountId The account ID
     * @param newBalance The new balance
     * @param userId The user ID (for security)
     * @throws IllegalArgumentException if account not found, access denied, or balance would be negative
     */
    public void updateBalance(Long accountId, BigDecimal newBalance, Long userId) {
        logger.info("Updating balance for account {} to {} for user: {}", accountId, newBalance, userId);
        
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            logger.error("Attempted to set negative balance {} for account: {}", newBalance, accountId);
            throw new IllegalArgumentException("Account balance cannot be negative");
        }

        Account account = getAccountById(accountId, userId);
        BigDecimal oldBalance = account.getBalance();
        
        account.setBalance(newBalance);
        accountRepository.save(account);
        
        logger.info("Balance updated for account {}: {} -> {}", accountId, oldBalance, newBalance);
    }

    /**
     * Check if user has sufficient balance in account for transfer.
     * Used before executing transfers to validate funds availability.
     * 
     * @param accountId The source account ID
     * @param amount The transfer amount
     * @param userId The user ID
     * @return true if sufficient balance, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean hasSufficientBalance(Long accountId, BigDecimal amount, Long userId) {
        logger.info("Checking sufficient balance for account {}: amount {}", accountId, amount);
        
        try {
            Account account = getAccountById(accountId, userId);
            boolean sufficient = account.getBalance().compareTo(amount) >= 0;
            
            logger.info("Balance check for account {}: current={}, required={}, sufficient={}", 
                       accountId, account.getBalance(), amount, sufficient);
            
            return sufficient;
        } catch (IllegalArgumentException e) {
            logger.error("Account not found during balance check: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get account count for a user.
     * Used for validation (ensure user has 2-3 accounts per FR-014).
     * 
     * @param userId The user ID
     * @return Number of accounts belonging to the user
     */
    @Transactional(readOnly = true)
    public long getAccountCount(Long userId) {
        logger.info("Counting accounts for user: {}", userId);
        
        long count = accountRepository.countByUserId(userId);
        
        logger.info("User {} has {} accounts", userId, count);
        return count;
    }

    /**
     * Create a new account for a user.
     * Used during user setup to create initial accounts.
     * 
     * @param userId The user ID
     * @param accountType The account type (CHECKING or SAVINGS)
     * @param accountName The display name for the account
     * @param initialBalance The initial balance
     * @return The created account
     */
    public Account createAccount(Long userId, String accountType, String accountName, BigDecimal initialBalance) {
        logger.info("Creating account for user {}: type={}, name={}, balance={}", 
                   userId, accountType, accountName, initialBalance);
        
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }

        Account account = new Account(userId, accountType, accountName, initialBalance);
        Account savedAccount = accountRepository.save(account);
        
        logger.info("Account created with ID: {} for user: {}", savedAccount.getId(), userId);
        return savedAccount;
    }
}