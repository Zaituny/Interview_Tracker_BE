package com.siemens.interviewTracker.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.siemens.interviewTracker.dto.InterviewProcessDTO;
import com.siemens.interviewTracker.entity.InterviewProcess;


@Mapper(componentModel = "spring")
public interface InterviewProcessMapper {

    InterviewProcessDTO interviewProcessToInterviewProcessDTO(InterviewProcess interviewProcess);

    InterviewProcess interviewProcessDTOToInterviewProcess(InterviewProcessDTO interviewProcessDTO);
}
