package com.siemens.interviewTracker.mapper;

import com.siemens.interviewTracker.entity.Candidate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import com.siemens.interviewTracker.dto.InterviewProcessDTO;
import com.siemens.interviewTracker.entity.InterviewProcess;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface InterviewProcessMapper {

    @Mapping(target = "candidateIds", source = "candidates", qualifiedByName = "mapCandidatesToIds")
    InterviewProcessDTO toDTO(InterviewProcess interviewProcess);

    InterviewProcess toEntity(InterviewProcessDTO interviewProcessDTO);

    @Named("mapCandidatesToIds")
    default Set<UUID> mapCandidatesToIds(Set<Candidate> candidates) {
        if (candidates == null) {
            return null;
        }
        return candidates.stream()
                .map(Candidate::getId)  // Extracting IDs from Candidate
                .collect(Collectors.toSet());
    }
}
