package org.itnaf.banking.service;

import lombok.RequiredArgsConstructor;
import org.itnaf.banking.model.User;
import org.itnaf.banking.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class for user management operations.
 * Handles user registration, authentication, and profile management.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user in the system.
     * Passwords are hashed using BCrypt before storage.
     *
     * @param user the user to register
     * @return the saved user entity
     * @throws IllegalArgumentException if email already exists
     */
    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    /**
     * Find a user by their email address.
     *
     * @param email the user's email
     * @return Optional containing the user if found
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Check if an email is already registered.
     *
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Update user's password.
     * The new password is hashed before storage.
     *
     * @param user the user whose password to update
     * @param newPassword the new plain-text password
     * @return the updated user
     */
    @Transactional
    public User updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
}
