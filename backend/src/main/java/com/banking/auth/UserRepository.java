package com.banking.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides data access operations for user authentication and management.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their username.
     * Used for authentication and user lookup operations.
     *
     * @param username the username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if a user exists with the given username.
     * Useful for registration validation.
     *
     * @param username the username to check
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Find a user by their account number.
     * Used for account-based operations.
     *
     * @param accountNumber the account number to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByAccountNumber(String accountNumber);

    /**
     * Check if a user exists with the given account number.
     * Useful for account validation.
     *
     * @param accountNumber the account number to check
     * @return true if account exists, false otherwise
     */
    boolean existsByAccountNumber(String accountNumber);
}