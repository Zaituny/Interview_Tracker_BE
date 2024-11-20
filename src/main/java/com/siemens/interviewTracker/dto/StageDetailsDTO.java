package com.siemens.interviewTracker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StageDetailsDTO {

    private String stageName;
    private Integer stageOrder;
    private long numberOfCandidates;
    private long numberOfInterviewers;
    private long numberOfCompletedInterviews;
    private long numberOfUpcomingInterviews;


    public StageDetailsDTO(String stageName,Integer stageOrder, long numberOfCandidates, long numberOfInterviewers, long numberOfCompletedInterviews, long numberOfUpcomingInterviews) {
        this.stageName = stageName;
        this.stageOrder = stageOrder;
        this.numberOfCandidates = numberOfCandidates;
        this.numberOfInterviewers = numberOfInterviewers;
        this.numberOfCompletedInterviews = numberOfCompletedInterviews;
        this.numberOfUpcomingInterviews = numberOfUpcomingInterviews;
    }

}
