package com.siemens.interviewTracker.controller;

import com.siemens.interviewTracker.dto.UserDTO;
import com.siemens.interviewTracker.service.AuthService;
import com.siemens.interviewTracker.entity.User;
import com.siemens.interviewTracker.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v0/auth")
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService, UserMapper userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@Valid @RequestBody UserDTO userDTO) {
        logger.info("Signing up user with email: {}", userDTO.getEmail());
        User createdUser = authService.signup(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.userToUserDTO(createdUser));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Signup failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
