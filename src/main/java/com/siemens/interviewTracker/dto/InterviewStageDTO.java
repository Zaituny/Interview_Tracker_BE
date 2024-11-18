package com.siemens.interviewTracker.dto;


import com.siemens.interviewTracker.entity.InterviewStageStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class InterviewStageDTO {

    private UUID id;

    @NotBlank(message = "Stage name cannot be empty")
    private String name;

    private UUID interviewProcessId;

    private InterviewStageStatus status;
}
