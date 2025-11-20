package com.banking.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AuthService.
 * Tests login validation and session token generation.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("demo");
        testUser.setPassword("password");
    }

    @Test
    void testLogin_Success_WithValidCredentials() {
        // Given: Valid credentials and user exists
        when(userRepository.findByUsername("demo")).thenReturn(Optional.of(testUser));

        // When: Attempting to log in
        LoginResponse result = authService.login("demo", "password");

        // Then: Login succeeds and returns token and username
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("demo");
        assertThat(result.getToken()).isNotNull();
        assertThat(result.getToken()).hasSizeGreaterThan(10); // Basic token validation
    }

    @Test
    void testLogin_Failure_WithInvalidUsername() {
        // Given: User does not exist
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then: Login attempt throws exception
        assertThatThrownBy(() -> authService.login("nonexistent", "password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    void testLogin_Failure_WithInvalidPassword() {
        // Given: User exists but password is wrong
        when(userRepository.findByUsername("demo")).thenReturn(Optional.of(testUser));

        // When & Then: Login attempt throws exception
        assertThatThrownBy(() -> authService.login("demo", "wrongpassword"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    void testLogin_Failure_WithEmptyUsername() {
        // When & Then: Login with empty username throws exception
        assertThatThrownBy(() -> authService.login("", "password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username and password are required");
    }

    @Test
    void testLogin_Failure_WithEmptyPassword() {
        // When & Then: Login with empty password throws exception
        assertThatThrownBy(() -> authService.login("demo", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username and password are required");
    }

    @Test
    void testLogin_Failure_WithNullCredentials() {
        // When & Then: Login with null credentials throws exception
        assertThatThrownBy(() -> authService.login(null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username and password are required");
    }

    @Test
    void testValidateSession_Success_WithValidToken() {
        // Given: A valid session token exists
        when(userRepository.findByUsername("demo")).thenReturn(Optional.of(testUser));
        LoginResponse loginResponse = authService.login("demo", "password");
        String token = loginResponse.getToken();

        // When: Validating the session
        boolean result = authService.validateSession(token);

        // Then: Session is valid
        assertThat(result).isTrue();
    }

    @Test
    void testValidateSession_Failure_WithInvalidToken() {
        // Given: An invalid token
        String invalidToken = "invalid-token-12345";

        // When: Validating the session
        boolean result = authService.validateSession(invalidToken);

        // Then: Session is invalid
        assertThat(result).isFalse();
    }

    @Test
    void testGetUserIdFromToken_Success() {
        // Given: A valid session exists
        when(userRepository.findByUsername("demo")).thenReturn(Optional.of(testUser));
        LoginResponse loginResponse = authService.login("demo", "password");
        String token = loginResponse.getToken();

        // When: Getting user ID from token
        Long userId = authService.getUserIdFromToken(token);

        // Then: Correct user ID is returned
        assertThat(userId).isEqualTo(1L);
    }

    @Test
    void testGetUserIdFromToken_Failure_WithInvalidToken() {
        // Given: An invalid token
        String invalidToken = "invalid-token-12345";

        // When & Then: Getting user ID throws exception
        assertThatThrownBy(() -> authService.getUserIdFromToken(invalidToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid session token");
    }
}