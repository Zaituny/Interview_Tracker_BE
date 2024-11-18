package com.siemens.interviewTracker.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import com.siemens.interviewTracker.dto.InterviewProcessDTO;
import com.siemens.interviewTracker.entity.InterviewProcess;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {CandidateMapper.class})
public interface InterviewProcessMapper {

    @Mapping(target = "candidates", qualifiedByName = "toCandidateDTOSet")
    InterviewProcessDTO toDTO(InterviewProcess interviewProcess);

    @Mapping(target = "candidates", ignore = true)
    InterviewProcess toEntity(InterviewProcessDTO interviewProcessDTO);
}