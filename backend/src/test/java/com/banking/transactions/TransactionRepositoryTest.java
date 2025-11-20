package com.banking.transactions;

import com.banking.auth.User;
import com.banking.auth.UserRepository;
import com.banking.accounts.Account;
import com.banking.accounts.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * T032 - TransactionRepository test (TDD Red Phase)
 * Tests for TransactionRepository JPA interface methods.
 * 
 * These tests will FAIL initially until Transaction entity and TransactionRepository are implemented.
 */
@DataJpaTest
@ActiveProfiles("test")
class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    private User testUser;
    private Account checkingAccount;
    private Account savingsAccount;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("testpass");
        testUser = entityManager.persistAndFlush(testUser);

        // Create test accounts
        checkingAccount = createTestAccount("Test Checking", "CHECKING", new BigDecimal("1000.00"));
        savingsAccount = createTestAccount("Test Savings", "SAVINGS", new BigDecimal("2500.00"));
        
        checkingAccount = entityManager.persistAndFlush(checkingAccount);
        savingsAccount = entityManager.persistAndFlush(savingsAccount);
    }

    @Test
    void shouldFindRecentTransactionsByAccountIds() {
        // Given - Create test transactions
        Transaction tx1 = createTransaction(checkingAccount.getId(), savingsAccount.getId(), 
            new BigDecimal("100.00"), "Transfer to savings", LocalDateTime.now().minusDays(5));
        Transaction tx2 = createTransaction(savingsAccount.getId(), checkingAccount.getId(), 
            new BigDecimal("50.00"), "Transfer to checking", LocalDateTime.now().minusDays(3));
        Transaction tx3 = createTransaction(checkingAccount.getId(), savingsAccount.getId(), 
            new BigDecimal("200.00"), "Another transfer", LocalDateTime.now().minusDays(1));
        
        entityManager.persistAndFlush(tx1);
        entityManager.persistAndFlush(tx2);
        entityManager.persistAndFlush(tx3);

        List<Long> accountIds = List.of(checkingAccount.getId(), savingsAccount.getId());
        Pageable pageable = PageRequest.of(0, 5);

        // When - Find recent transactions
        List<Transaction> transactions = transactionRepository.findRecentTransactionsByAccountIds(accountIds, pageable);

        // Then - Should return transactions in descending date order (most recent first)
        assertThat(transactions).hasSize(3);
        assertThat(transactions.get(0).getAmount()).isEqualByComparingTo(new BigDecimal("200.00"));
        assertThat(transactions.get(1).getAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(transactions.get(2).getAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    void shouldLimitTransactionsToPageableSize() {
        // Given - Create 7 transactions (more than limit of 5)
        for (int i = 0; i < 7; i++) {
            Transaction tx = createTransaction(checkingAccount.getId(), savingsAccount.getId(), 
                new BigDecimal("10.00"), "Test transfer " + i, LocalDateTime.now().minusDays(i));
            entityManager.persistAndFlush(tx);
        }

        List<Long> accountIds = List.of(checkingAccount.getId(), savingsAccount.getId());
        Pageable pageable = PageRequest.of(0, 5); // Limit to 5 per FR-006

        // When - Find recent transactions with limit
        List<Transaction> transactions = transactionRepository.findRecentTransactionsByAccountIds(accountIds, pageable);

        // Then - Should return exactly 5 transactions (most recent)
        assertThat(transactions).hasSize(5);
    }

    @Test
    void shouldReturnEmptyListForAccountsWithNoTransactions() {
        // Given - Accounts with no transactions (already created)
        List<Long> accountIds = List.of(checkingAccount.getId(), savingsAccount.getId());
        Pageable pageable = PageRequest.of(0, 5);

        // When - Find recent transactions
        List<Transaction> transactions = transactionRepository.findRecentTransactionsByAccountIds(accountIds, pageable);

        // Then - Should return empty list
        assertThat(transactions).isEmpty();
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

    private Transaction createTransaction(Long fromAccountId, Long toAccountId, BigDecimal amount, 
                                       String description, LocalDateTime transactionDate) {
        Transaction transaction = new Transaction();
        transaction.setFromAccountId(fromAccountId);
        transaction.setToAccountId(toAccountId);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setTransactionDate(transactionDate);
        return transaction;
    }
}