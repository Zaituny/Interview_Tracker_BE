package com.siemens.interviewTracker.dto;

import com.siemens.interviewTracker.entity.CandidateProcessStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CandidateStatusDTO {
    private UUID id;
    private UUID candidateId;
    private UUID interviewProcessId;
    private CandidateProcessStatus status;
    private UUID currentStageId;
}
