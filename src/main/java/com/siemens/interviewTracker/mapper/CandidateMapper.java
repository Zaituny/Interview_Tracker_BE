package com.siemens.interviewTracker.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import com.siemens.interviewTracker.dto.CandidateDTO;
import com.siemens.interviewTracker.entity.Candidate;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring", uses = {InterviewProcessMapper.class})
public interface CandidateMapper {

    @Mapping(target = "interviewProcesses", ignore = true)
    CandidateDTO toDTO(Candidate candidate);

    @Mapping(target = "interviewProcesses", ignore = true)
    Candidate toEntity(CandidateDTO candidateDTO);

    @Named("toCandidateDTOSet")
    default Set<CandidateDTO> toCandidateDTOSet(Set<Candidate> candidates) {
        return candidates.stream().map(this::toDTO).collect(Collectors.toSet());
    }

    @Named("toCandidateSet")
    default Set<Candidate> toCandidateSet(Set<CandidateDTO> candidateDTOs) {
        return candidateDTOs.stream().map(this::toEntity).collect(Collectors.toSet());
    }
}