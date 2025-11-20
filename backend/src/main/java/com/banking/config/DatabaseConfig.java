package com.banking.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Database configuration for SQLite and JPA setup.
 * 
 * Configures:
 * - Entity scanning for JPA entities
 * - Repository scanning for Spring Data JPA repositories
 * - Transaction management for ACID compliance
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.banking.repository")
@EntityScan(basePackages = "com.banking.model")
@EnableTransactionManagement
public class DatabaseConfig {

    /**
     * SQLite-specific configuration is handled via:
     * - application.yml: datasource.url, driver-class-name
     * - application.yml: jpa.properties.hibernate.dialect
     * 
     * No additional beans needed for basic SQLite setup.
     * Hibernate will auto-create tables based on @Entity classes.
     */
}