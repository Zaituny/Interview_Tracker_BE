package com.siemens.interviewTracker.dto;

import com.siemens.interviewTracker.entity.Candidate;
import com.siemens.interviewTracker.entity.InterviewStage;
import com.siemens.interviewTracker.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class FeedbackDTO {
    private UUID id;
    private UUID userId;
    private UUID candidateId;
    private UUID stageId;
    @NotBlank(message="Comments cannot be empty")
    private String comments;
}
