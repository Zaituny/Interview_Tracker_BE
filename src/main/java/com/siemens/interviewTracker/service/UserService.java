package com.siemens.interviewTracker.service;

import java.util.Set;
import java.util.UUID;
import jakarta.validation.Validator;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import jakarta.validation.ConstraintViolation;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import com.siemens.interviewTracker.entity.User;
import org.springframework.data.domain.PageRequest;
import com.siemens.interviewTracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, Validator validator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    public User createUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            String validationErrors = getValidationErrors(violations);
            throw new IllegalArgumentException("Validation errors: " + validationErrors);
        }

        validateRawPassword(user.getPassword());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Page<User> getAllUsers(int limit, int offset) {
        if (limit < 1 || offset < 0) {
            throw new IllegalArgumentException("Limit must be greater than 0 and offset must be non-negative.");
        }
        Pageable pageable = PageRequest.of(offset / limit, limit);
        return userRepository.findAll(pageable);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User updateUser(UUID id, User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        return userRepository.findById(id)
                .map(existingUser -> {
                    if (user.getEmail() != null) {
                        Set<ConstraintViolation<User>> violations = validator.validateProperty(user, "email");
                        if (!violations.isEmpty()) {
                            throw new IllegalArgumentException("Validation errors: " + getValidationErrors(violations));
                        }
                        existingUser.setEmail(user.getEmail());
                    }
                    if (user.getName() != null) {
                        Set<ConstraintViolation<User>> violations = validator.validateProperty(user, "name");
                        if (!violations.isEmpty()) {
                            throw new IllegalArgumentException("Validation errors: " + getValidationErrors(violations));
                        }
                        existingUser.setName(user.getName());
                    }
                    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                        validateRawPassword(user.getPassword());
                        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                    }
                    return userRepository.save(existingUser);
                })
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.deleteById(id);
    }

    private void validateRawPassword(String rawPassword) {
        if (rawPassword.length() < 8 || rawPassword.length() > 49) {
            throw new IllegalArgumentException("Password must be between 8 and 49 characters long");
        }

        if (!rawPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,100}$")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character.");
        }
    }

    private String getValidationErrors(Set<ConstraintViolation<User>> violations) {
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<User> violation : violations) {
            sb.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("\n");
        }
        return sb.toString();
    }
}
