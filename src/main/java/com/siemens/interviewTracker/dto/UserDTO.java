package com.siemens.interviewTracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UserDTO {

    @JsonIgnore
    private UUID id;

    @Email
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 4, max = 50)
    private String name;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    @JsonIgnore
    private String passwordToken;

    @JsonIgnore
    private LocalDateTime passwordTokenDate;
}
