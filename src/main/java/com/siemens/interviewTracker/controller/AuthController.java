package com.siemens.interviewTracker.controller;


import com.siemens.interviewTracker.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

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

