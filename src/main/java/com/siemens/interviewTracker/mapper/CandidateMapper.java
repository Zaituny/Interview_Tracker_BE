package com.siemens.interviewTracker.mapper;

import com.siemens.interviewTracker.dto.*;
import com.siemens.interviewTracker.entity.CandidateStatus;
import com.siemens.interviewTracker.entity.InterviewProcess;
import com.siemens.interviewTracker.entity.InterviewStage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.siemens.interviewTracker.entity.Candidate;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CandidateMapper {

    @Mapping(target = "interviewProcessIds", source = "interviewProcesses", qualifiedByName = "mapInterviewProcessesToIds")
    @Mapping(target = "interviewStageIds", source = "interviewStages", qualifiedByName = "mapInterviewStagesToIds")
    @Mapping(target = "candidateStatuses", source = "candidateStatuses", qualifiedByName = "mapCandidateStatusesToDTOs")
    CandidateDTO toDTO(Candidate candidate);

    Candidate toEntity(CandidateDTO candidateDTO);

    @Named("mapInterviewProcessesToIds")
    default Set<UUID> mapInterviewProcessesToIds(Set<InterviewProcess> interviewProcesses) {
        if (interviewProcesses == null) {
            return null;
        }
        return interviewProcesses.stream()
                .map(InterviewProcess::getId)
                .collect(Collectors.toSet());
    }

    @Named("mapInterviewStagesToIds")
    default Set<UUID> mapInterviewStagesToIds(Set<InterviewStage> interviewStages) {
        if (interviewStages == null) {
            return null;
        }
        return interviewStages.stream()
                .map(InterviewStage::getId)
                .collect(Collectors.toSet());
    }

    @Named("mapCandidateStatusesToDTOs")
    default Set<CandidateStatusDTO> mapCandidateStatusesToDTOs(Set<CandidateStatus> candidateStatuses) {
        if (candidateStatuses == null) {
            return null;
        }
        return candidateStatuses.stream()
                .map(candidateStatus -> {
                    CandidateStatusDTO dto = new CandidateStatusDTO();
                    dto.setId(candidateStatus.getId());
                    dto.setCandidateId(candidateStatus.getCandidate().getId());
                    dto.setInterviewProcessId(candidateStatus.getInterviewProcess().getId());
                    dto.setStatus(candidateStatus.getStatus());

                    // Map currentStage to currentStageId
                    if (candidateStatus.getCurrentStage() != null) {
                        dto.setCurrentStageId(candidateStatus.getCurrentStage().getId());
                    }

                    return dto;
                })
                .collect(Collectors.toSet());
    }

    @Mapping(target = "candidateId", source = "candidateDTO.id")
    @Mapping(target = "statusInProcess", source = "statusInProcess")
    @Mapping(target = "currentStageInProcess", source = "currentStageInProcess")
    CandidateInProcessDTO toCandidateInProcessDTO(CandidateDTO candidateDTO, String statusInProcess, String currentStageInProcess);

    @Mapping(target = "processes", source = "candidateStatuses", qualifiedByName = "mapCandidateStatusesToProcessDetailDTOs")
    CandidateProfileDTO toCandidateWithProcessesDTO(Candidate candidate);

    @Named("mapCandidateStatusesToProcessDetailDTOs")
    default List<ProcessDetailDTO> mapCandidateStatusesToProcessDetailDTOs(Set<CandidateStatus> candidateStatuses) {
        if (candidateStatuses == null) {
            return null;
        }
        return candidateStatuses.stream()
                .map(status -> {
                    ProcessDetailDTO dto = new ProcessDetailDTO();
                    dto.setProcessId(status.getInterviewProcess().getId());
                    dto.setProcessName(status.getInterviewProcess().getTitle());
                    dto.setCandidateStatus(status.getStatus().name());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
