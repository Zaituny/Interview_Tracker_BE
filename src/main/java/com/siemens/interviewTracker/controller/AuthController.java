package com.siemens.interviewTracker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.siemens.interviewTracker.dto.UserDTO;
import org.springframework.web.bind.annotation.*;
import com.siemens.interviewTracker.service.AuthService;
import com.siemens.interviewTracker.service.JwtTokenProvider;


@RestController
@RequestMapping("/api/v0/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserDTO> signup(@Valid @RequestBody UserDTO userDTO) {
        logger.info("Signing up user with email: {}", userDTO.getEmail());
        UserDTO createdUser = authService.signup(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestParam String email, @RequestParam String password) {
        logger.info("Logging in user with email: {}", email);
        UserDTO userDTO = authService.login(email, password);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String id) {
        logger.info("Logging out user with id: {}", id);
        authService.logout(id);
        return ResponseEntity.ok("Successfully logged out");
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String bearerToken = token.substring(7).trim();
            boolean isValid = jwtTokenProvider.validateToken(bearerToken);
            return ResponseEntity.ok(isValid);
        }
        return ResponseEntity.badRequest().body(false);
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<String> refreshToken(@RequestParam String refreshToken) {
        String newAccessToken = authService.generateAccessToken(refreshToken);
        return ResponseEntity.ok(newAccessToken);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        String response = authService.forgotPassword(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<UserDTO> resetPassword(@RequestParam String email, @RequestParam String newPassword,
                                                @RequestParam String resetPasswordToken) {
        UserDTO userDTO = authService.resetPassword(email, newPassword, resetPasswordToken);
        return ResponseEntity.ok(userDTO);
    }
}
