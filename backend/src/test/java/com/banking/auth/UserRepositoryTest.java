package com.banking.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for UserRepository JPA operations.
 * Tests findByUsername query method.
 */
@DataJpaTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUsername_ReturnsUser_WhenUserExists() {
        // Given: A user exists in the database
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");
        entityManager.persistAndFlush(user);

        // When: Finding user by username
        Optional<User> result = userRepository.findByUsername("testuser");

        // Then: User is found
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
        assertThat(result.get().getPassword()).isEqualTo("testpassword");
    }

    @Test
    void testFindByUsername_ReturnsEmpty_WhenUserDoesNotExist() {
        // Given: No user with the username exists
        
        // When: Finding user by username
        Optional<User> result = userRepository.findByUsername("nonexistent");

        // Then: Empty result is returned
        assertThat(result).isEmpty();
    }

    @Test
    void testFindByUsername_ReturnsCorrectUser_WhenMultipleUsersExist() {
        // Given: Multiple users exist
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("password1");
        entityManager.persistAndFlush(user1);

        User user2 = new User();
        user2.setUsername("user2"); 
        user2.setPassword("password2");
        entityManager.persistAndFlush(user2);

        // When: Finding specific user by username
        Optional<User> result = userRepository.findByUsername("user2");

        // Then: Correct user is returned
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("user2");
        assertThat(result.get().getPassword()).isEqualTo("password2");
    }
}