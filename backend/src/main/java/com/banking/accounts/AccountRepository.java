package com.banking.accounts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * T042 - AccountRepository interface for banking application.
 * 
 * Provides data access methods for Account entities.
 * Extends JpaRepository for basic CRUD operations with custom queries.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Find all accounts belonging to a specific user, ordered by creation date.
     * Used by dashboard to display user's accounts.
     * 
     * @param userId The ID of the user
     * @return List of accounts ordered by createdAt ascending
     */
    List<Account> findByUserIdOrderByCreatedAtAsc(Long userId);

    /**
     * Find an account by ID that belongs to a specific user.
     * Used for security - ensures users can only access their own accounts.
     * 
     * @param id The account ID
     * @param userId The user ID
     * @return Optional containing the account if found and belongs to user
     */
    Optional<Account> findByIdAndUserId(Long id, Long userId);

    /**
     * Check if a user has any accounts.
     * Useful for validation and user onboarding.
     * 
     * @param userId The user ID
     * @return true if user has at least one account
     */
    boolean existsByUserId(Long userId);

    /**
     * Count total accounts for a user.
     * Used for validation (ensure user has 2-3 accounts per FR-014).
     * 
     * @param userId The user ID
     * @return Number of accounts belonging to the user
     */
    long countByUserId(Long userId);

    /**
     * Find accounts by type for a specific user.
     * Useful for transfer operations to filter account selection.
     * 
     * @param userId The user ID
     * @param accountType The account type (CHECKING or SAVINGS)
     * @return List of matching accounts
     */
    List<Account> findByUserIdAndAccountType(Long userId, String accountType);
}