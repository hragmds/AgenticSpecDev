package com.banking.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Data initialization component for demo purposes.
 * Creates demo users when the application starts.
 */
@Component
@Profile("!test") // Don't run during tests
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing demo data...");

        // Check if demo user already exists
        if (userRepository.findByUsername("demo").isPresent()) {
            logger.info("Demo user already exists, skipping initialization");
            return;
        }

        // Create demo user
        User demoUser = new User(
                "demo",
                "password",
                "Demo User",
                "demo@example.com",
                "ACC001"
        );
        demoUser.setAccountBalance(1500.0);

        userRepository.save(demoUser);
        logger.info("Demo user created: username=demo, password=password, account=ACC001, balance=$1500.00");

        // Create additional demo user
        User johnUser = new User(
                "john",
                "password123",
                "John Smith",
                "john@example.com",
                "ACC002"
        );
        johnUser.setAccountBalance(2500.0);

        userRepository.save(johnUser);
        logger.info("Demo user created: username=john, password=password123, account=ACC002, balance=$2500.00");

        logger.info("Demo data initialization completed");
    }
}