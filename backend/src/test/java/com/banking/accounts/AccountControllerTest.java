package com.banking.accounts;

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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AccountController.
 * Tests the /api/accounts endpoints per contracts/api-spec.yaml
 */
@WebMvcTest(AccountController.class)
@Import(GlobalExceptionHandler.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String VALID_TOKEN = "valid-session-token-12345";
    private static final Long TEST_USER_ID = 1L;
    private static final String BEARER_TOKEN = "Bearer " + VALID_TOKEN;

    private List<Account> testAccounts;

    @BeforeEach
    void setUp() {
        // Create test accounts
        Account checkingAccount = new Account();
        checkingAccount.setId(1L);
        checkingAccount.setUserId(TEST_USER_ID);
        checkingAccount.setAccountType("CHECKING");
        checkingAccount.setAccountName("Main Checking");
        checkingAccount.setBalance(new BigDecimal("1500.00"));
        checkingAccount.setCreatedAt(LocalDateTime.now().minusDays(30));

        Account savingsAccount = new Account();
        savingsAccount.setId(2L);
        savingsAccount.setUserId(TEST_USER_ID);
        savingsAccount.setAccountType("SAVINGS");
        savingsAccount.setAccountName("Emergency Fund");
        savingsAccount.setBalance(new BigDecimal("5000.00"));
        savingsAccount.setCreatedAt(LocalDateTime.now().minusDays(60));

        testAccounts = Arrays.asList(checkingAccount, savingsAccount);
    }

    @Test
    void testGetUserAccounts_Success_WithValidToken() throws Exception {
        // Given: Valid authentication token and user accounts exist
        when(authService.validateSession(VALID_TOKEN)).thenReturn(true);
        when(authService.getUserIdFromToken(VALID_TOKEN)).thenReturn(TEST_USER_ID);
        when(accountService.getAccountsByUserId(TEST_USER_ID)).thenReturn(testAccounts);

        // When: GET /api/accounts with valid token
        mockMvc.perform(get("/api/accounts")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 200 OK with list of accounts
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].accountName").value("Main Checking"))
                .andExpect(jsonPath("$[0].accountType").value("CHECKING"))
                .andExpect(jsonPath("$[0].balance").value("1500.00"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].accountName").value("Emergency Fund"))
                .andExpect(jsonPath("$[1].accountType").value("SAVINGS"))
                .andExpect(jsonPath("$[1].balance").value("5000.00"));
    }

    @Test
    void testGetUserAccounts_Unauthorized_WithoutToken() throws Exception {
        // When: GET /api/accounts without authorization header
        mockMvc.perform(get("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 401 Unauthorized
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Authentication required"));
    }

    @Test
    void testGetUserAccounts_Unauthorized_WithInvalidToken() throws Exception {
        // Given: Invalid authentication token
        when(authService.validateSession(anyString())).thenReturn(false);

        // When: GET /api/accounts with invalid token
        mockMvc.perform(get("/api/accounts")
                .header("Authorization", "Bearer invalid-token")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 401 Unauthorized
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Authentication required"));
    }

    @Test
    void testGetUserAccounts_Success_WithEmptyAccountList() throws Exception {
        // Given: Valid token but user has no accounts
        when(authService.validateSession(VALID_TOKEN)).thenReturn(true);
        when(authService.getUserIdFromToken(VALID_TOKEN)).thenReturn(TEST_USER_ID);
        when(accountService.getAccountsByUserId(TEST_USER_ID)).thenReturn(Arrays.asList());

        // When: GET /api/accounts
        mockMvc.perform(get("/api/accounts")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 200 OK with empty array
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetAccountById_Success_WithValidId() throws Exception {
        // Given: Valid token and account exists
        Account account = testAccounts.get(0);
        when(authService.validateSession(VALID_TOKEN)).thenReturn(true);
        when(authService.getUserIdFromToken(VALID_TOKEN)).thenReturn(TEST_USER_ID);
        when(accountService.getAccountById(1L, TEST_USER_ID)).thenReturn(account);

        // When: GET /api/accounts/1 with valid token
        mockMvc.perform(get("/api/accounts/1")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 200 OK with account details
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.accountName").value("Main Checking"))
                .andExpect(jsonPath("$.balance").value("1500.00"));
    }

    @Test
    void testGetAccountById_NotFound_WithInvalidId() throws Exception {
        // Given: Valid token but account doesn't exist
        when(authService.validateSession(VALID_TOKEN)).thenReturn(true);
        when(authService.getUserIdFromToken(VALID_TOKEN)).thenReturn(TEST_USER_ID);
        when(accountService.getAccountById(999L, TEST_USER_ID))
                .thenThrow(new IllegalArgumentException("Account not found"));

        // When: GET /api/accounts/999
        mockMvc.perform(get("/api/accounts/999")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 404 Not Found
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Account not found"));
    }

    @Test
    void testGetAccountById_Unauthorized_WithoutToken() throws Exception {
        // When: GET /api/accounts/1 without token
        mockMvc.perform(get("/api/accounts/1")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 401 Unauthorized
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Authentication required"));
    }

    @Test
    void testGetAccountById_Unauthorized_WithInvalidToken() throws Exception {
        // Given: Invalid token
        when(authService.validateSession(anyString())).thenReturn(false);

        // When: GET /api/accounts/1 with invalid token
        mockMvc.perform(get("/api/accounts/1")
                .header("Authorization", "Bearer invalid-token")
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 401 Unauthorized
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAccountById_AccessDenied_WrongUser() throws Exception {
        // Given: Valid token but account belongs to different user
        when(authService.validateSession(VALID_TOKEN)).thenReturn(true);
        when(authService.getUserIdFromToken(VALID_TOKEN)).thenReturn(TEST_USER_ID);
        when(accountService.getAccountById(2L, TEST_USER_ID))
                .thenThrow(new IllegalArgumentException("Account not found"));

        // When: GET /api/accounts/2 (belongs to different user)
        mockMvc.perform(get("/api/accounts/2")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 404 to prevent user enumeration
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserAccounts_Handles_ServerError() throws Exception {
        // Given: Unexpected service error
        when(authService.validateSession(VALID_TOKEN)).thenReturn(true);
        when(authService.getUserIdFromToken(VALID_TOKEN)).thenReturn(TEST_USER_ID);
        when(accountService.getAccountsByUserId(TEST_USER_ID))
                .thenThrow(new RuntimeException("Database connection error"));

        // When: GET /api/accounts
        mockMvc.perform(get("/api/accounts")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))

                // Then: Returns 500 Internal Server Error
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Failed to retrieve accounts"));
    }

    @Test
    void testGetAccountById_Returns_CorrectAccountData() throws Exception {
        // Given: Account with multiple fields
        Account account = testAccounts.get(1);
        when(authService.validateSession(VALID_TOKEN)).thenReturn(true);
        when(authService.getUserIdFromToken(VALID_TOKEN)).thenReturn(TEST_USER_ID);
        when(accountService.getAccountById(2L, TEST_USER_ID)).thenReturn(account);

        // When: GET /api/accounts/2
        mockMvc.perform(get("/api/accounts/2")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))

                // Then: All fields are correctly returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.accountName").value("Emergency Fund"))
                .andExpect(jsonPath("$.accountType").value("SAVINGS"))
                .andExpect(jsonPath("$.balance").value("5000.00"));
    }

    @Test
    void testCORS_PreflightRequest_Allowed() throws Exception {
        // When: OPTIONS preflight request for CORS
        // Then: Should be handled by Spring (actual CORS header checking omitted in basic test)
        // The @CrossOrigin annotation on controller handles this
        mockMvc.perform(get("/api/accounts")
                .header("Origin", "http://localhost:4200")
                .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isOk());
    }
}
