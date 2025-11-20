package com.banking.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for handling user authentication and session management.
 * Provides login validation and session token generation.
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    // In-memory session storage for demo purposes
    // In production, this would be replaced with Redis or database storage
    private final Map<String, SessionInfo> activeSessions = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    /**
     * Session information for token validation
     */
    private static class SessionInfo {
        private final Long userId;
        private final String username;
        private final LocalDateTime createdAt;
        private final LocalDateTime expiresAt;

        public SessionInfo(Long userId, String username) {
            this.userId = userId;
            this.username = username;
            this.createdAt = LocalDateTime.now();
            this.expiresAt = LocalDateTime.now().plusHours(24); // 24-hour session
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }

        public Long getUserId() { return userId; }
        public String getUsername() { return username; }
    }

    /**
     * Authenticate user with username and password.
     *
     * @param username the username
     * @param password the password
     * @return LoginResponse with session token
     * @throws IllegalArgumentException if credentials are invalid
     */
    public LoginResponse login(String username, String password) {
        // Validate input parameters
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Username and password are required");
        }

        // Find user by username
        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        // Validate password (in production, use password hashing)
        if (!password.equals(user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // Check if user is active
        if (!user.getIsActive()) {
            throw new IllegalArgumentException("Account is inactive");
        }

        // Generate session token
        String token = generateSessionToken();
        
        // Store session information
        activeSessions.put(token, new SessionInfo(user.getId(), user.getUsername()));

        return new LoginResponse(user.getUsername(), token, "Login successful");
    }

    /**
     * Validate if a session token is valid and not expired.
     *
     * @param token the session token
     * @return true if session is valid, false otherwise
     */
    public boolean validateSession(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        SessionInfo session = activeSessions.get(token);
        if (session == null) {
            return false;
        }

        // Check if session has expired
        if (session.isExpired()) {
            activeSessions.remove(token);
            return false;
        }

        return true;
    }

    /**
     * Get user ID from valid session token.
     *
     * @param token the session token
     * @return user ID
     * @throws IllegalArgumentException if token is invalid
     */
    public Long getUserIdFromToken(String token) {
        if (!validateSession(token)) {
            throw new IllegalArgumentException("Invalid session token");
        }

        return activeSessions.get(token).getUserId();
    }

    /**
     * Get username from valid session token.
     *
     * @param token the session token
     * @return username
     * @throws IllegalArgumentException if token is invalid
     */
    public String getUsernameFromToken(String token) {
        if (!validateSession(token)) {
            throw new IllegalArgumentException("Invalid session token");
        }

        return activeSessions.get(token).getUsername();
    }

    /**
     * Logout user by invalidating their session token.
     *
     * @param token the session token to invalidate
     */
    public void logout(String token) {
        if (token != null && !token.trim().isEmpty()) {
            activeSessions.remove(token);
        }
    }

    /**
     * Generate a cryptographically secure session token.
     *
     * @return base64-encoded session token
     */
    private String generateSessionToken() {
        byte[] tokenBytes = new byte[32]; // 256-bit token
        random.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    /**
     * Clean up expired sessions (for maintenance).
     * In production, this would be handled by a scheduled task.
     */
    public void cleanupExpiredSessions() {
        activeSessions.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}