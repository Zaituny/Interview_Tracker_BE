package com.siemens.interviewTracker.mapper;

import com.siemens.interviewTracker.dto.InterviewStageDTO;
import com.siemens.interviewTracker.entity.Candidate;
import com.siemens.interviewTracker.entity.InterviewStage;
import com.siemens.interviewTracker.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface InterviewStageMapper {

    @Mapping(source = "interviewProcess.id", target = "interviewProcessId")
    @Mapping(target = "candidateIds", source = "candidates", qualifiedByName = "mapCandidatesToIds")
    @Mapping(target = "interviewerIds", source = "interviewers", qualifiedByName = "mapUsersToIds")
    @Mapping(target = "candidatesCount", expression = "java(interviewStage.getCandidates() != null ? interviewStage.getCandidates().size() : 0)")
    @Mapping(target = "interviewersCount", expression = "java(interviewStage.getInterviewers() != null ? interviewStage.getInterviewers().size() : 0)")
    InterviewStageDTO interviewStageToInterviewStageDTO(InterviewStage interviewStage);

    @Mapping(source = "interviewProcessId", target = "interviewProcess.id")
    InterviewStage interviewStageDTOToInterviewStage(InterviewStageDTO interviewStageDTO);

    @Named("mapCandidatesToIds")
    default Set<UUID> mapCandidatesToIds(Set<Candidate> candidates) {
        if (candidates == null) {
            return null;
        }
        return candidates.stream()
                .map(Candidate::getId)
                .collect(Collectors.toSet());
    }

    @Named("mapUsersToIds")
    default Set<UUID> mapUsersToIds(Set<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(User::getId)
                .collect(Collectors.toSet());
    }

    List<InterviewStageDTO> mapStagesToStageDTOs(List<InterviewStage> interviewStages);
}
