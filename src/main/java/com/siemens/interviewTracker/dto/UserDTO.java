package com.siemens.interviewTracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public class UserDTO {

    private UUID id;

    @Email
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 4, max = 50)
    private String name;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
