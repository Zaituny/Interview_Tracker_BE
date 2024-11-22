package com.siemens.interviewTracker.service;

import com.siemens.interviewTracker.dto.InterviewStageDTO;
import com.siemens.interviewTracker.entity.Candidate;
import com.siemens.interviewTracker.entity.InterviewProcess;
import com.siemens.interviewTracker.entity.InterviewStage;
import com.siemens.interviewTracker.entity.User;
import com.siemens.interviewTracker.mapper.InterviewStageMapper;
import com.siemens.interviewTracker.repository.CandidateRepository;
import com.siemens.interviewTracker.repository.InterviewProcessRepository;
import com.siemens.interviewTracker.repository.InterviewStageRepository;
import com.siemens.interviewTracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class InterviewStageService {

    private static final Logger logger = LoggerFactory.getLogger(InterviewStageService.class);

    private final InterviewProcessRepository interviewProcessRepository;
    private final InterviewStageRepository interviewStageRepository;
    private final InterviewStageMapper interviewStageMapper; // Inject Mapper

    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;

    public InterviewStageService(InterviewProcessRepository interviewProcessRepository,
                                 InterviewStageRepository interviewStageRepository,
                                 InterviewStageMapper interviewStageMapper,
                                 CandidateRepository candidateRepository,
                                 UserRepository userRepository) {
        this.interviewProcessRepository = interviewProcessRepository;
        this.interviewStageRepository = interviewStageRepository;
        this.interviewStageMapper = interviewStageMapper; // Initialize Mapper
        this.candidateRepository = candidateRepository;
        this.userRepository = userRepository;
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

    public void addInterviewerToStage(UUID stageId, UUID interviewerId) {
        // Fetch the stage by ID
        InterviewStage interviewStage = interviewStageRepository.findById(stageId)
                .orElseThrow(() -> new IllegalArgumentException("InterviewStage with ID " + stageId + " not found"));

        // Fetch the interviewer by ID
        User interviewer = userRepository.findById(interviewerId)
                .orElseThrow(() -> new IllegalArgumentException("Interviewer with ID " + interviewerId + " not found"));

        // Add the interviewer to the stage
        interviewStage.getInterviewers().add(interviewer);

        // Save the updated stage
        interviewStageRepository.save(interviewStage);

        logger.info("Added interviewer '{}' to stage '{}'", interviewer.getName(), interviewStage.getName());
    }

    public Set<UUID> getInterviewersForStage(UUID stageId) {
        // Fetch the stage by ID
        InterviewStage interviewStage = interviewStageRepository.findById(stageId)
                .orElseThrow(() -> new IllegalArgumentException("InterviewStage with ID " + stageId + " not found"));
        // Return the interviewers ids for the stage
        Set<UUID> interviewerIds = new HashSet<>();
        for (User interviewer : interviewStage.getInterviewers()) {
            interviewerIds.add(interviewer.getId());
        }
        return interviewerIds;
    }

    public Set<UUID> getCandidatesForStage(UUID stageId) {
        // Fetch the stage by ID
        InterviewStage interviewStage = interviewStageRepository.findById(stageId)
                .orElseThrow(() -> new IllegalArgumentException("InterviewStage with ID " + stageId + " not found"));

        // Return the candidates ids for the stage
        Set<UUID> candidateIds = new HashSet<>();
        for (Candidate candidate : interviewStage.getCandidates()) {
            candidateIds.add(candidate.getId());
        }
        return candidateIds;
    }

    public void deleteCandidateFromStage(UUID stageId, UUID candidateId) {
        // Fetch the stage by ID
        InterviewStage interviewStage = interviewStageRepository.findById(stageId)
                .orElseThrow(() -> new IllegalArgumentException("InterviewStage with ID " + stageId + " not found"));

        // Fetch the candidate by ID
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate with ID " + candidateId + " not found"));

        // Remove the candidate from the stage
        interviewStage.getCandidates().remove(candidate);

        // Save the updated stage
        interviewStageRepository.save(interviewStage);

        logger.info("Removed candidate '{}' from stage '{}'", candidate.getName(), interviewStage.getName());
    }

    public void deleteInterviewerFromStage(UUID stageId, UUID interviewerId) {
        // Fetch the stage by ID
        InterviewStage interviewStage = interviewStageRepository.findById(stageId)
                .orElseThrow(() -> new IllegalArgumentException("InterviewStage with ID " + stageId + " not found"));

        // Fetch the interviewer by ID
        User interviewer = userRepository.findById(interviewerId)
                .orElseThrow(() -> new IllegalArgumentException("Interviewer with ID " + interviewerId + " not found"));

        // Remove the interviewer from the stage
        interviewStage.getInterviewers().remove(interviewer);

        // Save the updated stage
        interviewStageRepository.save(interviewStage);

        logger.info("Removed interviewer '{}' from stage '{}'", interviewer.getName(), interviewStage.getName());
    }
}
