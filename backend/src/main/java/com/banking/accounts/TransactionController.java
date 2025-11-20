package com.banking.accounts;

import com.banking.auth.AuthService;
import com.banking.transactions.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * T047 - TransactionController implementation for banking application.
 * 
 * Provides REST endpoints for transaction operations per contracts/api-spec.yaml.
 * Implements GET /api/transactions and POST /api/transfers endpoints.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AuthService authService;

    /**
     * Get recent transactions for the authenticated user.
     * Implements GET /api/transactions per API specification.
     * 
     * @param authHeader Authorization header with Bearer token
     * @param limit Optional limit parameter (default 10)
     * @return ResponseEntity with list of transactions or error
     */
    @GetMapping("/transactions")
    public ResponseEntity<?> getRecentTransactions(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        
        logger.info("GET /api/transactions - retrieving recent transactions (limit: {})", limit);
        
        try {
            // Extract and validate token
            String token = extractTokenFromHeader(authHeader);
            if (token == null || !authService.validateSession(token)) {
                logger.warn("Unauthenticated request to /api/transactions");
                return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
            }

            // Get authenticated user ID
            Long userId = authService.getUserIdFromToken(token);

            // Get recent transactions
            List<Transaction> transactions = transactionService.getRecentTransactions(userId, limit);
            
            logger.info("Retrieved {} recent transactions for user: {}", transactions.size(), userId);
            return ResponseEntity.ok(transactions);
            
        } catch (Exception e) {
            logger.error("Error retrieving transactions: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to retrieve transactions"));
        }
    }

    /**
     * Get transactions for a specific account.
     * Implements GET /api/accounts/{id}/transactions per API specification.
     * 
     * @param accountId The account ID
     * @param authHeader Authorization header with Bearer token
     * @param limit Optional limit parameter (default 20)
     * @return ResponseEntity with list of transactions or error
     */
    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<?> getAccountTransactions(
            @PathVariable Long accountId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        
        logger.info("GET /api/accounts/{}/transactions - retrieving account transactions (limit: {})", 
                   accountId, limit);
        
        try {
            // Extract and validate token
            String token = extractTokenFromHeader(authHeader);
            if (token == null || !authService.validateSession(token)) {
                logger.warn("Unauthenticated request to /api/accounts/{}/transactions", accountId);
                return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
            }

            // Get authenticated user ID
            Long userId = authService.getUserIdFromToken(token);

            // Get account transactions (validates account ownership)
            List<Transaction> transactions = transactionService.getTransactionsByAccount(accountId, userId, limit);
            
            logger.info("Retrieved {} transactions for account {} (user: {})", 
                       transactions.size(), accountId, userId);
            return ResponseEntity.ok(transactions);
            
        } catch (IllegalArgumentException e) {
            logger.error("Account {} not found or access denied", accountId);
            return ResponseEntity.status(404).body(Map.of("error", "Account not found or access denied"));
        } catch (Exception e) {
            logger.error("Error retrieving account transactions: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to retrieve account transactions"));
        }
    }

    /**
     * Execute a money transfer between accounts.
     * Implements POST /api/transfers per API specification.
     * 
     * @param transferRequest The transfer request details
     * @param authHeader Authorization header with Bearer token
     * @return ResponseEntity with transaction details or error
     */
    @PostMapping("/transfers")
    public ResponseEntity<?> executeTransfer(
            @Valid @RequestBody TransferRequest transferRequest,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        logger.info("POST /api/transfers - executing transfer: from={}, to={}, amount={}", 
                   transferRequest.getFromAccountId(), transferRequest.getToAccountId(), 
                   transferRequest.getAmount());
        
        try {
            // Extract and validate token
            String token = extractTokenFromHeader(authHeader);
            if (token == null || !authService.validateSession(token)) {
                logger.warn("Unauthenticated request to /api/transfers");
                return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
            }

            // Get authenticated user ID
            Long userId = authService.getUserIdFromToken(token);

            // Execute transfer
            Transaction transaction = transactionService.executeTransfer(
                transferRequest.getFromAccountId(),
                transferRequest.getToAccountId(),
                transferRequest.getAmount(),
                transferRequest.getDescription(),
                userId
            );
            
            logger.info("Transfer executed successfully: transaction ID={}", transaction.getId());
            return ResponseEntity.ok(transaction);
            
        } catch (IllegalArgumentException e) {
            logger.error("Transfer validation failed: {}", e.getMessage());
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            logger.error("Transfer execution failed: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during transfer: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Transfer failed"));
        }
    }

    /**
     * Get transaction statistics for the user.
     * Provides transaction count and summary for analytics.
     * 
     * @param authHeader Authorization header with Bearer token
     * @return ResponseEntity with transaction statistics or error
     */
    @GetMapping("/transactions/stats")
    public ResponseEntity<?> getTransactionStats(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        logger.info("GET /api/transactions/stats - retrieving transaction statistics");
        
        try {
            // Extract and validate token
            String token = extractTokenFromHeader(authHeader);
            if (token == null || !authService.validateSession(token)) {
                logger.warn("Unauthenticated request to /api/transactions/stats");
                return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
            }

            // Get authenticated user ID
            Long userId = authService.getUserIdFromToken(token);

            // Get transaction statistics
            long transactionCount = transactionService.getTransactionCount(userId);
            List<Transaction> recentTransactions = transactionService.getRecentTransactions(userId, 5);
            
            Map<String, Object> stats = Map.of(
                "totalTransactions", transactionCount,
                "recentTransactions", recentTransactions,
                "recentCount", recentTransactions.size()
            );
            
            logger.info("Generated transaction stats for user {}: {} total transactions", 
                       userId, transactionCount);
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("Error generating transaction statistics: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to generate transaction statistics"));
        }
    }

    /**
     * Health check endpoint for transaction service.
     * Used by monitoring and frontend to verify service availability.
     * 
     * @return ResponseEntity with service status
     */
    @GetMapping("/transactions/health")
    public ResponseEntity<?> healthCheck() {
        logger.debug("GET /api/transactions/health - health check");
        
        return ResponseEntity.ok(Map.of(
            "service", "TransactionController",
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

    /**
     * DTO for transfer request payload.
     */
    public static class TransferRequest {
        
        @NotNull(message = "Source account ID is required")
        private Long fromAccountId;
        
        @NotNull(message = "Destination account ID is required")
        private Long toAccountId;
        
        @NotNull(message = "Transfer amount is required")
        @DecimalMin(value = "0.01", message = "Transfer amount must be at least $0.01")
        private BigDecimal amount;
        
        @NotBlank(message = "Transfer description is required")
        @Size(max = 255, message = "Description cannot exceed 255 characters")
        private String description;

        // Constructors
        public TransferRequest() {}

        public TransferRequest(Long fromAccountId, Long toAccountId, BigDecimal amount, String description) {
            this.fromAccountId = fromAccountId;
            this.toAccountId = toAccountId;
            this.amount = amount;
            this.description = description;
        }

        // Getters and Setters
        public Long getFromAccountId() { return fromAccountId; }
        public void setFromAccountId(Long fromAccountId) { this.fromAccountId = fromAccountId; }

        public Long getToAccountId() { return toAccountId; }
        public void setToAccountId(Long toAccountId) { this.toAccountId = toAccountId; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        @Override
        public String toString() {
            return "TransferRequest{" +
                    "fromAccountId=" + fromAccountId +
                    ", toAccountId=" + toAccountId +
                    ", amount=" + amount +
                    ", description='" + description + '\'' +
                    '}';
        }
    }
}