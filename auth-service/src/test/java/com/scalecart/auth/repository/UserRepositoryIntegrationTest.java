package com.scalecart.auth.repository;

import com.scalecart.auth.AbstractIntegrationTest;
import com.scalecart.auth.entity.Role;
import com.scalecart.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Transactional
@DisplayName("UserRepository Integration Tests")
class UserRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("integrationtest",
                "integration@test.com",
                "$2a$10$hashedpassword");
    }

    @Test
    @DisplayName("Should save and retrieve user by email")
    void saveAndFindByEmail() {
        userRepository.save(testUser);

        Optional<User> found = userRepository
                .findByEmail("integration@test.com");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername())
                .isEqualTo("integrationtest");
        assertThat(found.get().getEmail())
                .isEqualTo("integration@test.com");
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void findByEmail_NotFound() {
        Optional<User> found = userRepository
                .findByEmail("nonexistent@test.com");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should return true when email exists")
    void existsByEmail_True() {
        userRepository.save(testUser);

        boolean exists = userRepository
                .existsByEmail("integration@test.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when email does not exist")
    void existsByEmail_False() {
        boolean exists = userRepository
                .existsByEmail("nobody@test.com");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should enforce unique email constraint")
    void uniqueEmailConstraint() {
        userRepository.save(testUser);

        User duplicate = new User("otheruser",
                "integration@test.com",
                "$2a$10$different");

        assertThatThrownBy(() -> {
            userRepository.save(duplicate);
            userRepository.flush();
        }).isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should find roles seeded by Flyway migration")
    void flywaySeedData_RolesExist() {
        Optional<Role> userRole = roleRepository.findByName("ROLE_USER");
        Optional<Role> adminRole = roleRepository.findByName("ROLE_ADMIN");

        assertThat(userRole).isPresent();
        assertThat(adminRole).isPresent();
    }
}
