package com.siemens.interviewTracker.service;

import com.siemens.interviewTracker.entity.User;
import com.siemens.interviewTracker.dto.UserDTO;
import com.siemens.interviewTracker.repository.UserRepository;
import com.siemens.interviewTracker.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final UserMapper userMapper;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, Validator validator, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
        this.userMapper = userMapper;
    }

    public User signup(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException("Validation errors: " + violations);
        }

        validateRawPassword(userDTO.getPassword());

        User user = userMapper.userDTOToUser(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    private void validateRawPassword(String rawPassword) {
        if (rawPassword.length() < 8 || rawPassword.length() > 49) {
            throw new IllegalArgumentException("Password must be between 8 and 49 characters");
        }
        if (!rawPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,100}$")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase, lowercase, number, and special character");
        }
    }
}
