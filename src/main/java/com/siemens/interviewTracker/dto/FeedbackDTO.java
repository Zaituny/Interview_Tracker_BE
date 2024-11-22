package com.siemens.interviewTracker.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class FeedbackDTO {
    private UUID id;
    private String stageId;
    private String userId;
    private String candidateId;
    private String comments;
}
