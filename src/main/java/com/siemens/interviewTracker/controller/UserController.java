package com.siemens.interviewTracker.controller;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.siemens.interviewTracker.dto.UserDTO;
import org.springframework.web.bind.annotation.*;
import com.siemens.interviewTracker.service.UserService;
import org.springframework.validation.annotation.Validated;


@Validated
@RestController
@RequestMapping("/api/v0/users")
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        logger.info("Creating user with email: {}", userDTO.getEmail());
        UserDTO createdUser = userService.createUser(userDTO);
        logger.info("User created with ID: {}", createdUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestParam(defaultValue = "10") int limit,
                                                     @RequestParam(defaultValue = "0") int offset) {
        logger.info("Fetching users with limit: {}, offset: {}", limit, offset);
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100.");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be non-negative.");
        }

        List<UserDTO> users = userService.getAllUsers(limit, offset).getContent();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        logger.info("Fetching user with ID: {}", id);
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @RequestBody UserDTO userDTO) {
        logger.info("Updating user with ID: {}", id);
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        logger.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


}
