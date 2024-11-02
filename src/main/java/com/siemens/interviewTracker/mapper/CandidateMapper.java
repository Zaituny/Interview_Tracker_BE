package com.siemens.interviewTracker.mapper;

import org.mapstruct.Mapper;
import com.siemens.interviewTracker.dto.CandidateDTO;
import com.siemens.interviewTracker.entity.Candidate;


@Mapper(componentModel = "spring")
public interface CandidateMapper {

    CandidateDTO candidateToCandidateDTO(Candidate candidate);

    Candidate candidateDTOToCandidate(CandidateDTO candidateDTO);
}
