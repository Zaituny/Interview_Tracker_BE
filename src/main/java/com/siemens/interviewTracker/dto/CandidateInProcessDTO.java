package com.siemens.interviewTracker.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CandidateInProcessDTO {
    private UUID candidateId;
    private String email;
    private String name;
    private String phone;
    private String address;
    private String resumePath;
    private String statusInProcess;
    private String currentStageInProcess;
}
