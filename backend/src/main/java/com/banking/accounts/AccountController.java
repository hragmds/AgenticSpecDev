package com.banking.accounts;

import com.banking.auth.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * T046 - AccountController implementation for banking application.
 * 
 * Provides REST endpoints for account operations per contracts/api-spec.yaml.
 * Implements GET /api/accounts endpoint for FR-014 (Account Overview).
 */
@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthService authService;

    /**
     * Get all accounts for the authenticated user.
     * Implements GET /api/accounts per API specification.
     * 
     * @param authHeader Authorization header with Bearer token
     * @return ResponseEntity with list of accounts or error
     */
    @GetMapping
    public ResponseEntity<?> getUserAccounts(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info("GET /api/accounts - retrieving user accounts");
        
        try {
            // Extract and validate token
            String token = extractTokenFromHeader(authHeader);
            if (token == null || !authService.validateSession(token)) {
                logger.warn("Unauthenticated request to /api/accounts");
                return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
            }

            // Get authenticated user ID
            Long userId = authService.getUserIdFromToken(token);

            // Get user's accounts
            List<Account> accounts = accountService.getAccountsByUserId(userId);
            
            logger.info("Retrieved {} accounts for user: {}", accounts.size(), userId);
            return ResponseEntity.ok(accounts);
            
        } catch (Exception e) {
            logger.error("Error retrieving accounts: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to retrieve accounts"));
        }
    }

    /**
     * Get a specific account by ID for the authenticated user.
     * Implements GET /api/accounts/{id} per API specification.
     * 
     * @param accountId The account ID
     * @param authHeader Authorization header with Bearer token
     * @return ResponseEntity with account details or error
     */
    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccountById(@PathVariable Long accountId, 
                                          @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info("GET /api/accounts/{} - retrieving account details", accountId);
        
        try {
            // Extract and validate token
            String token = extractTokenFromHeader(authHeader);
            if (token == null || !authService.validateSession(token)) {
                logger.warn("Unauthenticated request to /api/accounts/{}", accountId);
                return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
            }

            // Get authenticated user ID
            Long userId = authService.getUserIdFromToken(token);

            // Get specific account (validates ownership)
            Account account = accountService.getAccountById(accountId, userId);
            
            logger.info("Retrieved account {} for user: {}", accountId, userId);
            return ResponseEntity.ok(account);
            
        } catch (IllegalArgumentException e) {
            logger.error("Account {} not found or access denied for user", accountId);
            return ResponseEntity.status(404).body(Map.of("error", "Account not found"));
        } catch (Exception e) {
            logger.error("Error retrieving account {}: {}", accountId, e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to retrieve account"));
        }
    }

    /**
     * Get account summary statistics.
     * Provides total account count and balance summary for dashboard.
     * 
     * @param authHeader Authorization header with Bearer token
     * @return ResponseEntity with account summary or error
     */
    @GetMapping("/summary")
    public ResponseEntity<?> getAccountSummary(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info("GET /api/accounts/summary - retrieving account summary");
        
        try {
            // Extract and validate token
            String token = extractTokenFromHeader(authHeader);
            if (token == null || !authService.validateSession(token)) {
                logger.warn("Unauthenticated request to /api/accounts/summary");
                return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
            }

            // Get authenticated user ID
            Long userId = authService.getUserIdFromToken(token);

            // Get user's accounts and calculate summary
            List<Account> accounts = accountService.getAccountsByUserId(userId);
            long accountCount = accounts.size();
            double totalBalance = accounts.stream()
                .mapToDouble(account -> account.getBalance().doubleValue())
                .sum();
            
            Map<String, Object> summary = Map.of(
                "accountCount", accountCount,
                "totalBalance", totalBalance,
                "accounts", accounts
            );
            
            logger.info("Generated account summary for user {}: {} accounts, ${}", 
                       userId, accountCount, totalBalance);
            return ResponseEntity.ok(summary);
            
        } catch (Exception e) {
            logger.error("Error generating account summary: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to generate account summary"));
        }
    }

    /**
     * Health check endpoint for account service.
     * Used by monitoring and frontend to verify service availability.
     * 
     * @return ResponseEntity with service status
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        logger.debug("GET /api/accounts/health - health check");
        
        return ResponseEntity.ok(Map.of(
            "service", "AccountController",
            "status", "healthy",
            "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * Extract session token from Authorization header.
     * Handles both "Bearer token" and direct token formats.
     * 
     * @param authHeader the Authorization header value
     * @return the extracted token or null if invalid
     */
    private String extractTokenFromHeader(String authHeader) {
        try {
            if (authHeader == null || authHeader.trim().isEmpty()) {
                return null;
            }

            if (authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7); // Remove "Bearer " prefix
            }

            return authHeader; // Direct token format
        } catch (Exception e) {
            logger.warn("Failed to extract token from Authorization header: {}", e.getMessage());
            return null;
        }
    }
}