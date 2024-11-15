package com.siemens.interviewTracker.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.siemens.interviewTracker.dto.InterviewProcessDTO;
import com.siemens.interviewTracker.entity.InterviewProcess;


@Mapper(componentModel = "spring")
public interface InterviewProcessMapper {

    @Mapping(source = "jobPosition.id", target = "jobPositionId")
    InterviewProcessDTO interviewProcessToInterviewProcessDTO(InterviewProcess interviewProcess);

    @Mapping(source = "jobPositionId", target = "jobPosition.id")
    InterviewProcess interviewProcessDTOToInterviewProcess(InterviewProcessDTO interviewProcessDTO);
}
