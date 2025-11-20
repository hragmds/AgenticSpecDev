package com.banking.accounts;

import com.banking.auth.User;
import com.banking.auth.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * T031 - AccountRepository test (TDD Red Phase)
 * Tests for AccountRepository JPA interface methods.
 * 
 * These tests will FAIL initially until Account entity and AccountRepository are implemented.
 */
@DataJpaTest
@ActiveProfiles("test")
class AccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("testpass");
        testUser = entityManager.persistAndFlush(testUser);
    }

    @Test
    void shouldFindAccountsByUserId() {
        // Given - Create test accounts for user
        Account checkingAccount = createTestAccount("Test Checking", "CHECKING", new BigDecimal("1000.00"));
        Account savingsAccount = createTestAccount("Test Savings", "SAVINGS", new BigDecimal("2500.50"));
        
        entityManager.persistAndFlush(checkingAccount);
        entityManager.persistAndFlush(savingsAccount);

        // When - Find accounts by user ID
        List<Account> accounts = accountRepository.findByUserIdOrderByCreatedAtAsc(testUser.getId());

        // Then - Should return both accounts in creation order
        assertThat(accounts).hasSize(2);
        assertThat(accounts.get(0).getAccountName()).isEqualTo("Test Checking");
        assertThat(accounts.get(0).getAccountType()).isEqualTo("CHECKING");
        assertThat(accounts.get(0).getBalance()).isEqualByComparingTo(new BigDecimal("1000.00"));
        
        assertThat(accounts.get(1).getAccountName()).isEqualTo("Test Savings");
        assertThat(accounts.get(1).getAccountType()).isEqualTo("SAVINGS");
        assertThat(accounts.get(1).getBalance()).isEqualByComparingTo(new BigDecimal("2500.50"));
    }

    @Test
    void shouldReturnEmptyListForUserWithNoAccounts() {
        // Given - User with no accounts (testUser already created)
        
        // When - Find accounts by user ID
        List<Account> accounts = accountRepository.findByUserIdOrderByCreatedAtAsc(testUser.getId());

        // Then - Should return empty list
        assertThat(accounts).isEmpty();
    }

    @Test
    void shouldFindAccountByIdAndUserId() {
        // Given - Create test account
        Account account = createTestAccount("Test Account", "CHECKING", new BigDecimal("500.00"));
        account = entityManager.persistAndFlush(account);

        // When - Find account by ID and user ID
        Account foundAccount = accountRepository.findByIdAndUserId(account.getId(), testUser.getId()).orElse(null);

        // Then - Should find the account
        assertThat(foundAccount).isNotNull();
        assertThat(foundAccount.getAccountName()).isEqualTo("Test Account");
        assertThat(foundAccount.getUserId()).isEqualTo(testUser.getId());
    }

    @Test
    void shouldNotFindAccountForWrongUser() {
        // Given - Create account for testUser
        Account account = createTestAccount("Test Account", "CHECKING", new BigDecimal("500.00"));
        account = entityManager.persistAndFlush(account);

        // Create another user
        User otherUser = new User();
        otherUser.setUsername("otheruser");
        otherUser.setPassword("otherpass");
        otherUser = entityManager.persistAndFlush(otherUser);

        // When - Try to find account with wrong user ID
        Account foundAccount = accountRepository.findByIdAndUserId(account.getId(), otherUser.getId()).orElse(null);

        // Then - Should not find the account
        assertThat(foundAccount).isNull();
    }

    private Account createTestAccount(String name, String type, BigDecimal balance) {
        Account account = new Account();
        account.setUserId(testUser.getId());
        account.setAccountName(name);
        account.setAccountType(type);
        account.setBalance(balance);
        account.setCreatedAt(LocalDateTime.now());
        return account;
    }
}