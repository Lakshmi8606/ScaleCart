package com.scalecart.auth.service;

import com.scalecart.auth.entity.Role;
import com.scalecart.auth.entity.User;
import com.scalecart.auth.repository.RoleRepository;
import com.scalecart.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(UserRepository userRepository,
                                   RoleRepository roleRepository,
                                   PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerNewUser(String username, String email, String rawPassword) {

        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email already registered: " + email);
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalStateException("Username already taken: " + username);
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);

        // NEVER store the raw password - always hash it with BCrypt
        user.setPassword(passwordEncoder.encode(rawPassword));

        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Default role ROLE_USER not found in DB"));

        Set<Role> roles = new HashSet<>();
        roles.add(defaultRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }
}