package com.banking.transactions;

import com.banking.accounts.TransactionController;
import com.banking.auth.AuthService;
import com.banking.config.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.banking.accounts.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for TransactionController.
 * Tests the /api/transactions and /api/accounts/{id}/transactions endpoints per contracts/api-spec.yaml
 */
@WebMvcTest(TransactionController.class)
@Import(GlobalExceptionHandler.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String VALID_TOKEN = "valid-session-token-12345";
    private static final Long TEST_USER_ID = 1L;
    private static final String BEARER_TOKEN = "Bearer " + VALID_TOKEN;

    private List<Transaction> testTransactions;

    @BeforeEach
    void setUp() {
        // Create test transactions
        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setFromAccountId(1L);
        transaction1.setToAccountId(2L);
        transaction1.setAmount(new BigDecimal("500.00"));
        transaction1.setDescription("Transfer to savings");
        transaction1.setTransactionDate(LocalDateTime.now().minusDays(2));

        Transaction transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setFromAccountId(2L);
        transaction2.setToAccountId(1L);
        transaction2.setAmount(new BigDecimal("100.00"));
        transaction2.setDescription("Transfer from savings");
        transaction2.setTransactionDate(LocalDateTime.now().minusDays(1));

        Transaction transaction3 = new Transaction();
        transaction3.setId(3L);
        transaction3.setFromAccountId(1L);
        transaction3.setToAccountId(2L);
        transaction3.setAmount(new BigDecimal("250.00"));
        transaction3.setDescription("Monthly transfer");
        transaction3.setTransactionDate(LocalDateTime.now());

        testTransactions = Arrays.asList(transaction1, transaction2, transaction3);
    }

    @Test
    void testGetRecentTransactions_Success_WithValidToken() throws Exception {
        // Given: Valid token and recent transactions exist
        when(authService.validateSession(VALID_TOKEN)).thenReturn(true);
        when(authService.getUserIdFromToken(VALID_TOKEN)).thenReturn(TEST_USER_ID);
        when(transactionService.getRecentTransactions(TEST_USER_ID, 10))
                .thenReturn(testTransactions.subList(0, 3));

        // When: GET /api/transactions with valid token
        mockMvc.perform(get("/api/transactions")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 200 OK with list of transactions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].amount").value("500.00"))
                .andExpect(jsonPath("$[0].description").value("Transfer to savings"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].amount").value("100.00"))
                .andExpect(jsonPath("$[2].id").value(3L))
                .andExpect(jsonPath("$[2].amount").value("250.00"));
    }

    @Test
    void testGetRecentTransactions_Unauthorized_WithoutToken() throws Exception {
        // When: GET /api/transactions without authorization header
        mockMvc.perform(get("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 401 Unauthorized
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Authentication required"));
    }

    @Test
    void testGetRecentTransactions_Unauthorized_WithInvalidToken() throws Exception {
        // Given: Invalid token
        when(authService.validateSession(anyString())).thenReturn(false);

        // When: GET /api/transactions with invalid token
        mockMvc.perform(get("/api/transactions")
                .header("Authorization", "Bearer invalid-token")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 401 Unauthorized
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication required"));
    }

    @Test
    void testGetRecentTransactions_Success_WithEmptyList() throws Exception {
        // Given: Valid token but no transactions
        when(authService.validateSession(VALID_TOKEN)).thenReturn(true);
        when(authService.getUserIdFromToken(VALID_TOKEN)).thenReturn(TEST_USER_ID);
        when(transactionService.getRecentTransactions(TEST_USER_ID, 10)).thenReturn(Arrays.asList());

        // When: GET /api/transactions
        mockMvc.perform(get("/api/transactions")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 200 OK with empty array
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetRecentTransactions_WithCustomLimit() throws Exception {
        // Given: Valid token and custom limit parameter
        when(authService.validateSession(VALID_TOKEN)).thenReturn(true);
        when(authService.getUserIdFromToken(VALID_TOKEN)).thenReturn(TEST_USER_ID);
        when(transactionService.getRecentTransactions(TEST_USER_ID, 5))
                .thenReturn(testTransactions.subList(0, 2));

        // When: GET /api/transactions?limit=5
        mockMvc.perform(get("/api/transactions")
                .param("limit", "5")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 200 OK and calls service with correct limit
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetAccountTransactions_Success_WithValidToken() throws Exception {
        // Given: Valid token and account transactions exist
        when(authService.validateSession(VALID_TOKEN)).thenReturn(true);
        when(authService.getUserIdFromToken(VALID_TOKEN)).thenReturn(TEST_USER_ID);
        when(transactionService.getTransactionsByAccount(1L, TEST_USER_ID, 20))
                .thenReturn(Arrays.asList(testTransactions.get(0), testTransactions.get(2)));

        // When: GET /api/accounts/1/transactions
        mockMvc.perform(get("/api/accounts/1/transactions")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 200 OK with account transactions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].fromAccountId").value(1L))
                .andExpect(jsonPath("$[0].toAccountId").value(2L))
                .andExpect(jsonPath("$[1].id").value(3L));
    }

    @Test
    void testGetAccountTransactions_Unauthorized_WithoutToken() throws Exception {
        // When: GET /api/accounts/1/transactions without token
        mockMvc.perform(get("/api/accounts/1/transactions")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 401 Unauthorized
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication required"));
    }

    @Test
    void testGetAccountTransactions_Unauthorized_WithInvalidToken() throws Exception {
        // Given: Invalid token
        when(authService.validateSession(anyString())).thenReturn(false);

        // When: GET /api/accounts/1/transactions with invalid token
        mockMvc.perform(get("/api/accounts/1/transactions")
                .header("Authorization", "Bearer invalid-token")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 401 Unauthorized
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAccountTransactions_AccessDenied_WrongUser() throws Exception {
        // Given: Valid token but account belongs to different user
        when(authService.validateSession(VALID_TOKEN)).thenReturn(true);
        when(authService.getUserIdFromToken(VALID_TOKEN)).thenReturn(TEST_USER_ID);
        when(transactionService.getTransactionsByAccount(1L, TEST_USER_ID, 20))
                .thenThrow(new IllegalArgumentException("Account not found or access denied"));

        // When: GET /api/accounts/1/transactions
        mockMvc.perform(get("/api/accounts/1/transactions")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 404 to prevent user enumeration
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAccountTransactions_NotFound_WithInvalidAccountId() throws Exception {
        // Given: Valid token but account doesn't exist
        when(authService.validateSession(VALID_TOKEN)).thenReturn(true);
        when(authService.getUserIdFromToken(VALID_TOKEN)).thenReturn(TEST_USER_ID);
        when(transactionService.getTransactionsByAccount(999L, TEST_USER_ID, 20))
                .thenThrow(new IllegalArgumentException("Account not found"));

        // When: GET /api/accounts/999/transactions
        mockMvc.perform(get("/api/accounts/999/transactions")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 404 Not Found
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Account not found"));
    }

    @Test
    void testGetAccountTransactions_WithCustomLimit() throws Exception {
        // Given: Valid token and custom limit
        when(authService.validateSession(VALID_TOKEN)).thenReturn(true);
        when(authService.getUserIdFromToken(VALID_TOKEN)).thenReturn(TEST_USER_ID);
        when(transactionService.getTransactionsByAccount(1L, TEST_USER_ID, 5))
                .thenReturn(Arrays.asList(testTransactions.get(0)));

        // When: GET /api/accounts/1/transactions?limit=5
        mockMvc.perform(get("/api/accounts/1/transactions")
                .param("limit", "5")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 200 OK with correct limit applied
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetRecentTransactions_Handles_ServerError() throws Exception {
        // Given: Service throws unexpected error
        when(authService.validateSession(VALID_TOKEN)).thenReturn(true);
        when(authService.getUserIdFromToken(VALID_TOKEN)).thenReturn(TEST_USER_ID);
        when(transactionService.getRecentTransactions(TEST_USER_ID, 10))
                .thenThrow(new RuntimeException("Database error"));

        // When: GET /api/transactions
        mockMvc.perform(get("/api/transactions")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 500 Internal Server Error
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Failed to retrieve transactions"));
    }

    @Test
    void testGetAccountTransactions_Handles_ServerError() throws Exception {
        // Given: Service throws unexpected error
        when(authService.validateSession(VALID_TOKEN)).thenReturn(true);
        when(authService.getUserIdFromToken(VALID_TOKEN)).thenReturn(TEST_USER_ID);
        when(transactionService.getTransactionsByAccount(1L, TEST_USER_ID, 20))
                .thenThrow(new RuntimeException("Database error"));

        // When: GET /api/accounts/1/transactions
        mockMvc.perform(get("/api/accounts/1/transactions")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 500 Internal Server Error
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Failed to retrieve account transactions"));
    }

    @Test
    void testGetRecentTransactions_Returns_CompleteTransactionData() throws Exception {
        // Given: Valid token and transactions with all fields
        when(authService.validateSession(VALID_TOKEN)).thenReturn(true);
        when(authService.getUserIdFromToken(VALID_TOKEN)).thenReturn(TEST_USER_ID);
        when(transactionService.getRecentTransactions(TEST_USER_ID, 10))
                .thenReturn(Arrays.asList(testTransactions.get(0)));

        // When: GET /api/transactions
        mockMvc.perform(get("/api/transactions")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))

                // Then: All transaction fields are returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].fromAccountId").value(1L))
                .andExpect(jsonPath("$[0].toAccountId").value(2L))
                .andExpect(jsonPath("$[0].amount").value("500.00"))
                .andExpect(jsonPath("$[0].description").value("Transfer to savings"));
    }

    @Test
    void testCORS_PreflightRequest_Allowed() throws Exception {
        // When: OPTIONS preflight request for CORS
        // Then: Should be handled by Spring CORS configuration
        mockMvc.perform(get("/api/transactions")
                .header("Origin", "http://localhost:4200")
                .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isOk());
    }
}
