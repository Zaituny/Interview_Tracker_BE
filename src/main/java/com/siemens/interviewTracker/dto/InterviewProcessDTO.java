package com.siemens.interviewTracker.dto;

import com.siemens.interviewTracker.entity.InterviewProcessStatus;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;


@Getter
@Setter
public class InterviewProcessDTO {
    private UUID id;

    @NotBlank(message = "Title cannot be empty")
    private String title;

    private InterviewProcessStatus status;

    @Email
    @NotBlank(message = "created by email cannot be empty")
    private String createdBy;

    private LocalDateTime createdAt;

    private Set<CandidateDTO> candidates;
}