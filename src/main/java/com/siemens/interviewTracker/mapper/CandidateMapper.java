package com.siemens.interviewTracker.mapper;

import com.siemens.interviewTracker.entity.InterviewProcess;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.siemens.interviewTracker.dto.CandidateDTO;
import com.siemens.interviewTracker.entity.Candidate;
import org.mapstruct.Named;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface CandidateMapper {

    @Mapping(target = "interviewProcessIds", source = "interviewProcesses", qualifiedByName = "mapInterviewProcessesToIds")
    CandidateDTO toDTO(Candidate candidate);

    Candidate toEntity(CandidateDTO candidateDTO);

    Set<CandidateDTO> toDTOSet(Set<Candidate> candidates);

    Set<Candidate> toEntitySet(Set<CandidateDTO> candidateDTOs);

    @Named("mapInterviewProcessesToIds")
    default Set<UUID> mapInterviewProcessesToIds(Set<InterviewProcess> interviewProcesses) {
        if (interviewProcesses == null) {
            return null;
        }
        return interviewProcesses.stream()
                .map(InterviewProcess::getId)  // Extracting IDs from InterviewProcess
                .collect(Collectors.toSet());
    }
}