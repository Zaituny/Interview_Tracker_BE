package com.siemens.interviewTracker.service;

import com.siemens.interviewTracker.dto.InterviewProcessDTO;
import com.siemens.interviewTracker.dto.InterviewStageDTO;
import com.siemens.interviewTracker.entity.Candidate;
import com.siemens.interviewTracker.entity.InterviewProcess;
import com.siemens.interviewTracker.entity.InterviewStage;
import com.siemens.interviewTracker.entity.User;
import com.siemens.interviewTracker.exception.UserNotFoundException;
import com.siemens.interviewTracker.mapper.InterviewStageMapper;
import com.siemens.interviewTracker.repository.CandidateRepository;
import com.siemens.interviewTracker.repository.InterviewProcessRepository;
import com.siemens.interviewTracker.repository.InterviewStageRepository;
import com.siemens.interviewTracker.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.siemens.interviewTracker.utils.ValidationUtils.getValidationErrors;

@Service
@Transactional
public class InterviewStageService {

    private static final Logger logger = LoggerFactory.getLogger(InterviewStageService.class);

    private final InterviewProcessRepository interviewProcessRepository;
    private final InterviewStageRepository interviewStageRepository;
    private final InterviewStageMapper interviewStageMapper; // Inject Mapper

    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;

    private final Validator validator;

    public InterviewStageService(InterviewProcessRepository interviewProcessRepository,
                                 InterviewStageRepository interviewStageRepository,
                                 InterviewStageMapper interviewStageMapper,
                                 CandidateRepository candidateRepository,
                                 UserRepository userRepository, Validator validator) {
        this.interviewProcessRepository = interviewProcessRepository;
        this.interviewStageRepository = interviewStageRepository;
        this.interviewStageMapper = interviewStageMapper; // Initialize Mapper
        this.candidateRepository = candidateRepository;
        this.userRepository = userRepository;
        this.validator = validator;
    }

    public Page<InterviewStageDTO> getAllInterviewStages(int limit, int offset) {
        logger.debug("Fetching all interview stages with limit: {}, offset: {}", limit, offset);

        if (limit < 1 || offset < 0) {
            throw new IllegalArgumentException("Limit must be greater than 0 and offset must be non-negative.");
        }

        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<InterviewStage> interviewStages = interviewStageRepository.findAll(pageable);
        return interviewStages.map(interviewStageMapper::interviewStageToInterviewStageDTO);
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

    public long getCandidateCountInStage(UUID interviewStageId) {
        logger.info("Fetching candidate count for interview stage ID: {}", interviewStageId);
        InterviewStage interviewStage = interviewStageRepository.findById(interviewStageId)
                .orElseThrow(() -> new IllegalArgumentException("InterviewStage with ID " + interviewStageId + " not found"));

        long candidateCount = interviewStage.getCandidates().size();
        logger.info("Candidate count for stage '{}': {}", interviewStage.getName(), candidateCount);
        return candidateCount;
    }

    public long getInterviewerCountInStage(UUID interviewStageId) {
        logger.info("Fetching interviewer count for interview stage ID: {}", interviewStageId);
        InterviewStage interviewStage = interviewStageRepository.findById(interviewStageId)
                .orElseThrow(() -> new IllegalArgumentException("InterviewStage with ID " + interviewStageId + " not found"));

        long interviewerCount = interviewStage.getInterviewers().size();
        logger.info("Interviewer count for stage '{}': {}", interviewStage.getName(), interviewerCount);
        return interviewerCount;
    }

    @Transactional
    public void deleteInterviewStage(UUID stageId) {
        logger.info("Attempting to delete interview stage with ID: {}", stageId);

        // Fetch the stage to ensure it exists
        InterviewStage stage = interviewStageRepository.findById(stageId)
                .orElseThrow(() -> new IllegalArgumentException("InterviewStage with ID " + stageId + " not found"));

        // Restrict deletion if the stage has candidates
        if (!stage.getCandidates().isEmpty()) {
            throw new IllegalStateException("Cannot delete stage with ID " + stageId + " because it has associated candidates.");
        }

        // Cascade stage order numbering for other stages in the process
        cascadeStageOrderAfterDeletion(stage);

        // Delete the stage
        interviewStageRepository.delete(stage);
        logger.info("Deleted interview stage with ID: {}", stageId);
    }

    private void cascadeStageOrderAfterDeletion(InterviewStage stage) {
        logger.info("Cascading stage order numbering for process ID: {}", stage.getInterviewProcess().getId());

        // Get all stages in the process with stage_order greater than the deleted stage
        List<InterviewStage> subsequentStages = interviewStageRepository.findByInterviewProcessIdAndStageOrderGreaterThan(
                stage.getInterviewProcess().getId(), stage.getStageOrder());

        // Decrement the stage order for each subsequent stage
        subsequentStages.forEach(subsequentStage -> {
            subsequentStage.setStageOrder(subsequentStage.getStageOrder() - 1);
            interviewStageRepository.save(subsequentStage); // Save changes
        });

        logger.info("Stage order numbering adjusted successfully.");
    }

    public void addInterviewersToInterviewStage(UUID stageId, List<UUID> interviewerIds){
        logger.debug("Adding interviewers to stage with ID: {}", stageId);
        InterviewStage interviewStage = interviewStageRepository.findById(stageId)
                .orElseThrow(() -> new IllegalArgumentException("InterviewStage with ID " + stageId + " not found"));

        List<User> interviewers = userRepository.findAllById(interviewerIds);

        if(interviewers.size() != interviewerIds.size()){
            logger.error("Some interviewers not found expected: {} found: {}", interviewerIds.size(), interviewers.size());
            throw new UserNotFoundException("One or more interviewers not found expected: " + interviewerIds.size() + " found: " + interviewers.size());
        }

        interviewStage.getInterviewers().addAll(interviewers);

        interviewStageRepository.save(interviewStage);
        logger.info("Added interviewers to stage with ID: {}", stageId);
    }

    @Transactional
    public InterviewStageDTO updateInterviewStage(UUID stageId, InterviewStageDTO stageDTO) {
        logger.debug("Updating interview stage with ID: {}", stageId);

        if (stageDTO == null) {
            throw new IllegalArgumentException("InterviewStageDTO cannot be null");
        }

        return interviewStageRepository.findById(stageId)
                .map(existingStage -> {
                    // Update fields only if provided in the DTO

                    if (stageDTO.getName() != null) {
                        Set<ConstraintViolation<InterviewStageDTO>> nameViolations = validator.validateProperty(stageDTO, "name");
                        if (!nameViolations.isEmpty()) {
                            throw new IllegalArgumentException("Validation errors in 'name': " + getValidationErrors(nameViolations));
                        }
                        existingStage.setName(stageDTO.getName());
                    }


                    if (stageDTO.getDescription() != null) {
                        Set<ConstraintViolation<InterviewStageDTO>> descViolations = validator.validateProperty(stageDTO, "description");
                        if (!descViolations.isEmpty()) {
                            throw new IllegalArgumentException("Validation errors in 'description': " + getValidationErrors(descViolations));
                        }
                        existingStage.setDescription(stageDTO.getDescription());
                    }

                    if (stageDTO.getStatus() != null) {
                        existingStage.setStatus(stageDTO.getStatus());
                    }

                    // Save updated stage
                    InterviewStage updatedStage = interviewStageRepository.save(existingStage);
                    logger.info("Interview stage with ID {} updated successfully", stageId);

                    return updatedStage;
                })
                .map(interviewStageMapper::interviewStageToInterviewStageDTO)
                .orElseThrow(() -> {
                    logger.error("InterviewStage not found with ID: {}", stageId);
                    return new IllegalArgumentException("InterviewStage with ID " + stageId + " not found");
                });
    }
}
