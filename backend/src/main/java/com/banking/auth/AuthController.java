package com.banking.auth;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints.
 * Handles user login and session management.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * Login endpoint for user authentication.
     *
     * @param loginRequest the login credentials
     * @param bindingResult validation results
     * @return LoginResponse with session token or error
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            BindingResult bindingResult) {

        logger.info("Login attempt for username: {}", loginRequest.getUsername());

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            throw new IllegalArgumentException(errorMessage);
        }

        try {
            LoginResponse response = authService.login(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            logger.info("Successful login for username: {}", loginRequest.getUsername());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Failed login attempt for username: {} - {}", 
                       loginRequest.getUsername(), e.getMessage());
            throw e; // Let GlobalExceptionHandler handle this
        }
    }

    /**
     * Validate session token endpoint.
     *
     * @param token the session token from Authorization header
     * @return validation result
     */
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateSession(
            @RequestHeader("Authorization") String token) {

        logger.debug("Session validation request");

        // Extract token from "Bearer <token>" format
        String sessionToken = extractTokenFromHeader(token);
        
        boolean isValid = authService.validateSession(sessionToken);
        
        logger.debug("Session validation result: {}", isValid);
        return ResponseEntity.ok(isValid);
    }

    /**
     * Logout endpoint to invalidate session.
     *
     * @param token the session token from Authorization header
     * @return success message
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String token) {

        logger.info("Logout request received");

        // Extract token from "Bearer <token>" format
        String sessionToken = extractTokenFromHeader(token);
        
        authService.logout(sessionToken);
        
        logger.info("User logged out successfully");
        return ResponseEntity.ok("Logged out successfully");
    }

    /**
     * Get current user information from session token.
     *
     * @param token the session token from Authorization header
     * @return user information
     */
    @GetMapping("/me")
    public ResponseEntity<UserInfo> getCurrentUser(
            @RequestHeader("Authorization") String token) {

        logger.debug("Get current user request");

        try {
            String sessionToken = extractTokenFromHeader(token);
            
            if (!authService.validateSession(sessionToken)) {
                throw new IllegalArgumentException("Invalid or expired session token");
            }

            Long userId = authService.getUserIdFromToken(sessionToken);
            String username = authService.getUsernameFromToken(sessionToken);

            UserInfo userInfo = new UserInfo(userId, username);
            
            logger.debug("Current user info retrieved for: {}", username);
            return ResponseEntity.ok(userInfo);

        } catch (IllegalArgumentException e) {
            logger.warn("Failed to get current user: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Extract session token from Authorization header.
     * Supports both "Bearer <token>" and direct token formats.
     *
     * @param authHeader the Authorization header value
     * @return the extracted token
     */
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || authHeader.trim().isEmpty()) {
            throw new IllegalArgumentException("Authorization header is required");
        }

        if (authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer " prefix
        }

        return authHeader; // Direct token format
    }

    /**
     * Simple DTO for current user information.
     */
    public static class UserInfo {
        private Long userId;
        private String username;

        public UserInfo(Long userId, String username) {
            this.userId = userId;
            this.username = username;
        }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }
}