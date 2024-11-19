package com.siemens.interviewTracker.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class FeedbackItemDTO {
    private UUID id;
    private String feedbackFormId;
    private String question;
    private String answer;
}
