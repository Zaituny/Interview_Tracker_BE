package com.siemens.interviewTracker.controller;

import com.siemens.interviewTracker.dto.UserDTO;

import com.siemens.interviewTracker.service.AuthService;
import com.siemens.interviewTracker.entity.User;
import com.siemens.interviewTracker.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
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

    // Endpoint for forgot password, which generates and sends the reset token
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        String response = authService.forgotPass(email);

        if (response.equals("Invalid email id.")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("The provided email address is not associated with any account.");
        }

        return ResponseEntity.ok(response);
    }

    // Endpoint to validate the reset token
    @GetMapping("/validate-token")
    public ResponseEntity<Boolean> validateToken(@RequestParam String email, @RequestParam String token) {
        boolean isValid = authService.validateToken(email, token);
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(isValid);
        }
        return ResponseEntity.ok(isValid);
    }

    // Endpoint to reset the password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        String response = authService.resetPass(email, newPassword);
        if (response.equals("Invalid email")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("The provided email address is not associated with any account.");
        }
        return ResponseEntity.ok(response);
    }
}
