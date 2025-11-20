package com.banking.auth;

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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AuthController.
 * Tests the login endpoint and error handling.
 */
@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequest validLoginRequest;
    private LoginResponse validLoginResponse;

    @BeforeEach
    void setUp() {
        validLoginRequest = new LoginRequest();
        validLoginRequest.setUsername("demo");
        validLoginRequest.setPassword("password");

        validLoginResponse = new LoginResponse();
        validLoginResponse.setUsername("demo");
        validLoginResponse.setToken("mock-session-token-12345");
    }

    @Test
    void testLogin_Success_WithValidCredentials() throws Exception {
        // Given: AuthService returns successful response
        when(authService.login("demo", "password")).thenReturn(validLoginResponse);

        // When: POST to /auth/login with valid credentials
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginRequest)))
                
                // Then: Returns 200 OK with login response
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("demo"))
                .andExpect(jsonPath("$.token").value("mock-session-token-12345"));
    }

    @Test
    void testLogin_Failure_WithInvalidCredentials() throws Exception {
        // Given: AuthService throws exception for invalid credentials
        when(authService.login(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Invalid username or password"));

        // When: POST to /auth/login with invalid credentials
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginRequest)))
                
                // Then: Returns 400 Bad Request with error message
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid username or password"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testLogin_Failure_WithEmptyUsername() throws Exception {
        // Given: Request with empty username
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setUsername("");
        invalidRequest.setPassword("password");

        // When: POST to /auth/login with empty username
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                
                // Then: Returns 400 Bad Request
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Username is required"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testLogin_Failure_WithEmptyPassword() throws Exception {
        // Given: Request with empty password
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setUsername("demo");
        invalidRequest.setPassword("");

        // When: POST to /auth/login with empty password
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                
                // Then: Returns 400 Bad Request
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Password is required"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testLogin_Failure_WithMalformedJson() throws Exception {
        // Given: Malformed JSON request
        String malformedJson = "{ \"username\": \"demo\", \"password\":";

        // When: POST to /auth/login with malformed JSON
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                
                // Then: Returns 400 Bad Request
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testLogin_Failure_WithMissingContentType() throws Exception {
        // When: POST to /auth/login without Content-Type header
        mockMvc.perform(post("/auth/login")
                .content(objectMapper.writeValueAsString(validLoginRequest)))
                
                // Then: Returns 415 Unsupported Media Type
                .andExpect(status().isUnsupportedMediaType());
    }
}