package com.siemens.interviewTracker.service;

import com.siemens.interviewTracker.dto.InterviewStageDTO;
import com.siemens.interviewTracker.entity.Candidate;
import com.siemens.interviewTracker.entity.InterviewProcess;
import com.siemens.interviewTracker.entity.InterviewStage;
import com.siemens.interviewTracker.mapper.InterviewStageMapper;
import com.siemens.interviewTracker.repository.CandidateRepository;
import com.siemens.interviewTracker.repository.InterviewProcessRepository;
import com.siemens.interviewTracker.repository.InterviewStageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.UUID;

@Service
@Transactional
public class InterviewStageService {

    private static final Logger logger = LoggerFactory.getLogger(InterviewStageService.class);

    private final InterviewProcessRepository interviewProcessRepository;
    private final InterviewStageRepository interviewStageRepository;
    private final InterviewStageMapper interviewStageMapper; // Inject Mapper

    private final CandidateRepository candidateRepository;

    public InterviewStageService(InterviewProcessRepository interviewProcessRepository,
                                 InterviewStageRepository interviewStageRepository,
                                 InterviewStageMapper interviewStageMapper,
                                 CandidateRepository candidateRepository) {
        this.interviewProcessRepository = interviewProcessRepository;
        this.interviewStageRepository = interviewStageRepository;
        this.interviewStageMapper = interviewStageMapper; // Initialize Mapper
        this.candidateRepository = candidateRepository;
    }



    public void addCandidateToStage(UUID stageId, UUID candidateId) {
        // Fetch the stage by ID
        InterviewStage interviewStage = interviewStageRepository.findById(stageId)
                .orElseThrow(() -> new IllegalArgumentException("InterviewStage with ID " + stageId + " not found"));

        // Fetch the candidate by ID
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate with ID " + candidateId + " not found"));

        // Validate that the candidate is part of the same interview process
        if (!interviewStage.getInterviewProcess().getCandidates().contains(candidate)) {
            throw new IllegalArgumentException("Candidate with ID " + candidateId + " is not part of the interview process");
        }

        // Add the candidate to the stage
        interviewStage.getCandidates().add(candidate);

        // Save the updated stage
        interviewStageRepository.save(interviewStage);

        logger.info("Added candidate '{}' to stage '{}'", candidate.getName(), interviewStage.getName());
    }


}
