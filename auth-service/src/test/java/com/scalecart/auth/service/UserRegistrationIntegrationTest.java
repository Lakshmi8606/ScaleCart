package com.scalecart.auth.service;

import com.scalecart.auth.AbstractIntegrationTest;
import com.scalecart.auth.entity.User;
import com.scalecart.auth.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@Transactional
@DisplayName("UserRegistration Integration Tests — full stack")
class UserRegistrationIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserRegistrationService userRegistrationService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Full registration flow — user saved with hashed password and ROLE_USER")
    void register_FullFlow_UserSavedCorrectly() {
        User registered = userRegistrationService.registerNewUser(
                "fullflowuser",
                "fullflow@test.com",
                "RawPassword123"
        );

        assertThat(registered.getId()).isNotNull();

        User fromDb = userRepository
                .findByEmail("fullflow@test.com")
                .orElseThrow();

        assertThat(fromDb.getPassword()).startsWith("$2a$");
        assertThat(fromDb.getPassword())
                .doesNotContain("RawPassword123");

        assertThat(fromDb.getRoles())
                .extracting("name")
                .contains("ROLE_USER");
    }

    @Test
    @DisplayName("Should prevent duplicate registration with same email")
    void register_DuplicateEmail_ThrowsException() {
        userRegistrationService.registerNewUser(
                "user1", "duplicate@test.com", "Password123");

        assertThatThrownBy(() ->
                userRegistrationService.registerNewUser(
                        "user2", "duplicate@test.com", "Password456"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Email already registered");

        long count = userRepository.findAll().stream()
                .filter(u -> "duplicate@test.com".equals(u.getEmail()))
                .count();
        assertThat(count).isEqualTo(1);
    }
}
