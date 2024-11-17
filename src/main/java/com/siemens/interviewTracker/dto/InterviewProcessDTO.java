package com.siemens.interviewTracker.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;


@Getter
@Setter
public class InterviewProcessDTO {
    private UUID id;

    @NotBlank(message = "Title cannot be empty")
    private String title;

    private String status;

    private UUID jobPositionId;
}
