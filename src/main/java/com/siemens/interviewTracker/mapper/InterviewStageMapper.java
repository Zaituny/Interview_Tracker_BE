package com.siemens.interviewTracker.mapper;

import com.siemens.interviewTracker.dto.InterviewStageDTO;
import com.siemens.interviewTracker.entity.InterviewStage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InterviewStageMapper {

    InterviewStageDTO interviewStageToInterviewStageDTO(InterviewStage interviewStage);

    InterviewStage interviewStageDTOToInterviewStage(InterviewStageDTO interviewStageDTO);

}
