package com.siemens.interviewTracker.mapper;

import com.siemens.interviewTracker.dto.InterviewStageDTO;
import com.siemens.interviewTracker.entity.InterviewStage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InterviewStageMapper {

    @Mapping(source = "interviewProcess.id", target = "interviewProcessId")
    InterviewStageDTO interviewStageToInterviewStageDTO(InterviewStage interviewStage);

    @Mapping(source = "interviewProcessId", target = "interviewProcess.id")
    InterviewStage interviewStageDTOToInterviewStage(InterviewStageDTO interviewStageDTO);

}
