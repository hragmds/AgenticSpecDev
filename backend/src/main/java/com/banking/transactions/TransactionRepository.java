package com.banking.transactions;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * T043 - TransactionRepository interface for banking application.
 * 
 * Provides data access methods for Transaction entities.
 * Implements custom queries for transaction history and recent transactions.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Find recent transactions for given account IDs, ordered by date descending.
     * Used by dashboard to show last 5 transactions per FR-006.
     * 
     * @param accountIds List of account IDs to search for
     * @param pageable Pagination parameters (limit to 5 transactions)
     * @return List of recent transactions ordered by date descending
     */
    @Query("SELECT t FROM Transaction t " +
           "WHERE t.fromAccountId IN :accountIds OR t.toAccountId IN :accountIds " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findRecentTransactionsByAccountIds(@Param("accountIds") List<Long> accountIds, 
                                                        Pageable pageable);

    /**
     * Find all transactions for a specific account (as source or destination).
     * Used for detailed transaction history.
     * 
     * @param accountId The account ID
     * @param pageable Pagination parameters
     * @return List of transactions involving the account
     */
    @Query("SELECT t FROM Transaction t " +
           "WHERE t.fromAccountId = :accountId OR t.toAccountId = :accountId " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findByAccountId(@Param("accountId") Long accountId, Pageable pageable);

    /**
     * Find transactions between two specific accounts.
     * Used for transfer history analysis.
     * 
     * @param accountId1 First account ID
     * @param accountId2 Second account ID
     * @param pageable Pagination parameters
     * @return List of transactions between the accounts
     */
    @Query("SELECT t FROM Transaction t " +
           "WHERE (t.fromAccountId = :accountId1 AND t.toAccountId = :accountId2) " +
           "OR (t.fromAccountId = :accountId2 AND t.toAccountId = :accountId1) " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findBetweenAccounts(@Param("accountId1") Long accountId1, 
                                         @Param("accountId2") Long accountId2, 
                                         Pageable pageable);

    /**
     * Count total transactions for an account.
     * Used for statistics and pagination.
     * 
     * @param accountId The account ID
     * @return Total number of transactions involving the account
     */
    @Query("SELECT COUNT(t) FROM Transaction t " +
           "WHERE t.fromAccountId = :accountId OR t.toAccountId = :accountId")
    long countByAccountId(@Param("accountId") Long accountId);
}