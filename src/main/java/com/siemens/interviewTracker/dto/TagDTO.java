package com.siemens.interviewTracker.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

@Getter
@Setter
public class TagDTO {

    private UUID id;

    @NotBlank(message = "Tag name cannot be empty")
    private String name;
}
