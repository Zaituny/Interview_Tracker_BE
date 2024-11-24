package com.siemens.interviewTracker.mapper;

import com.siemens.interviewTracker.dto.CandidateStatusDTO;
import com.siemens.interviewTracker.dto.InterviewProcessDTO;
import com.siemens.interviewTracker.entity.Candidate;
import com.siemens.interviewTracker.entity.CandidateStatus;
import com.siemens.interviewTracker.entity.InterviewProcess;
import com.siemens.interviewTracker.entity.InterviewStage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface InterviewProcessMapper {

    // Map candidates, stages, and statuses to their respective DTOs
    @Mapping(target = "candidateIds", source = "candidates", qualifiedByName = "mapCandidatesToIds")
    @Mapping(target = "interviewStageIds", source = "interviewStages", qualifiedByName = "mapStagesToIds")
    @Mapping(target = "candidateStatuses", source = "candidateStatuses", qualifiedByName = "mapStatusesToDTOs")
    InterviewProcessDTO toDTO(InterviewProcess interviewProcess);

    InterviewProcess toEntity(InterviewProcessDTO interviewProcessDTO);

    // Helper method to map candidates to UUIDs
    @Named("mapCandidatesToIds")
    default Set<UUID> mapCandidatesToIds(Set<Candidate> candidates) {
        if (candidates == null) {
            return null;
        }
        return candidates.stream()
                .map(Candidate::getId)
                .collect(Collectors.toSet());
    }

    // Helper method to map stages to UUIDs
    @Named("mapStagesToIds")
    default Set<UUID> mapStagesToIds(Set<InterviewStage> stages) {
        if (stages == null) {
            return null;
        }
        return stages.stream()
                .map(InterviewStage::getId)
                .collect(Collectors.toSet());
    }

    // Helper method to map candidate statuses to DTOs
    @Named("mapStatusesToDTOs")
    default Set<CandidateStatusDTO> mapStatusesToDTOs(Set<CandidateStatus> statuses) {
        if (statuses == null) {
            return null;
        }
        return statuses.stream()
                .map(status -> {
                    CandidateStatusDTO dto = new CandidateStatusDTO();
                    dto.setId(status.getId());
                    dto.setCandidateId(status.getCandidate().getId());
                    dto.setStatus(status.getStatus());
                    dto.setInterviewProcessId(status.getInterviewProcess().getId());
                    return dto;
                })
                .collect(Collectors.toSet());
    }
}
