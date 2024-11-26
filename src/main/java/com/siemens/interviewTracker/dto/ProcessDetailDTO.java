package com.siemens.interviewTracker.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProcessDetailDTO {
    private UUID processId;
    private String processName;
    private String candidateStatus;
}
