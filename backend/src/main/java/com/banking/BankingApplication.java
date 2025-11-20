package com.banking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for Banking MVP.
 * 
 * Features:
 * - Auto-configuration for web, JPA, and database
 * - Component scanning for controllers, services, repositories
 * - Database initialization via application.yml
 */
@SpringBootApplication
public class BankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
    }
}