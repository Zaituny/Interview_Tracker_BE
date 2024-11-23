package com.siemens.interviewTracker.dto;


import com.siemens.interviewTracker.entity.Candidate;
import com.siemens.interviewTracker.entity.InterviewStageStatus;
import com.siemens.interviewTracker.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class InterviewStageDTO {

    private UUID id;

    @NotBlank(message = "Stage name cannot be empty")
    private String name;

    @NotBlank(message = "Stage description cannot be empty")
    private String description;

    private UUID interviewProcessId;

    private InterviewStageStatus status;

    private Integer stageOrder;

    private Set<UUID> candidateIds;
    private Set<UUID> interviewerIds;

    private long candidatesCount;
    private long interviewersCount;
}
