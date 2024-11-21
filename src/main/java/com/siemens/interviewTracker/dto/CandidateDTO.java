package com.siemens.interviewTracker.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


@Getter
@Setter
public class CandidateDTO {

    @Id
    private UUID id;

    @Email
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @Size(min = 4, max = 50)
    @NotBlank(message = "Name cannot be empty")
    private String name;

    private String phone;

    private String address;

    private String resumePath;

    private Set<UUID> interviewProcessIds;

    private Set<UUID> interviewStageIds;
}