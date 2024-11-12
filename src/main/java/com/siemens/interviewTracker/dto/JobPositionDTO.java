package com.siemens.interviewTracker.dto;


import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class JobPositionDTO {

    @Id
    private UUID id;

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    private String status;

    private String requirements;

    private String responsibilities;

}
