package com.siemens.interviewTracker.dto;

import com.siemens.interviewTracker.entity.InterviewStageStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StageDetailsDTO {

    private String stageName;
    private UUID stageId;
    private Integer stageOrder;
    private InterviewStageStatus stageStatus;
    private String description;
    private long numberOfCandidates;
    private long numberOfInterviewers;


    public StageDetailsDTO(String stageName,UUID stageId, InterviewStageStatus stageStatus,  Integer stageOrder,String description, long numberOfCandidates, long numberOfInterviewers) {
        this.stageName = stageName;
        this.stageOrder = stageOrder;
        this.stageId = stageId;
        this.stageStatus = stageStatus;
        this.description = description;
        this.numberOfCandidates = numberOfCandidates;
        this.numberOfInterviewers = numberOfInterviewers;
    }

}
