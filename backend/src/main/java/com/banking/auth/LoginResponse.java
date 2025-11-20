package com.banking.auth;

/**
 * Login response data transfer object for authentication API.
 * Contains successful login information including session token.
 */
public class LoginResponse {

    private String username;
    private String token;
    private String message;

    // Default constructor
    public LoginResponse() {
    }

    // Constructor for successful login
    public LoginResponse(String username, String token) {
        this.username = username;
        this.token = token;
        this.message = "Login successful";
    }

    // Constructor with custom message
    public LoginResponse(String username, String token, String message) {
        this.username = username;
        this.token = token;
        this.message = message;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "username='" + username + '\'' +
                ", token='[PROTECTED]'" +
                ", message='" + message + '\'' +
                '}';
    }
}