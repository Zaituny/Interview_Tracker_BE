package com.siemens.interviewTracker.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.siemens.interviewTracker.dto.CandidateDTO;
import com.siemens.interviewTracker.dto.InterviewStageDTO;
import com.siemens.interviewTracker.entity.Candidate;
import com.siemens.interviewTracker.entity.InterviewStage;
import com.siemens.interviewTracker.mapper.CandidateMapper;
import com.siemens.interviewTracker.mapper.InterviewStageMapper;
import com.siemens.interviewTracker.repository.CandidateRepository;
import com.siemens.interviewTracker.repository.InterviewStageRepository;

import com.siemens.interviewTracker.dto.StageDetailsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Validator;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import jakarta.validation.ConstraintViolation;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import com.siemens.interviewTracker.dto.InterviewProcessDTO;
import com.siemens.interviewTracker.entity.InterviewProcess;
import com.siemens.interviewTracker.mapper.InterviewProcessMapper;
import com.siemens.interviewTracker.repository.InterviewProcessRepository;

import static com.siemens.interviewTracker.entity.InterviewProcessStatus.*;
import static com.siemens.interviewTracker.utils.ValidationUtils.getValidationErrors;


@Service
@Transactional
public class InterviewProcessService {
    private final Validator validator;
    private final InterviewProcessMapper interviewProcessMapper;

    private final InterviewStageMapper interviewStageMapper;
    private final InterviewProcessRepository interviewProcessRepository;

    private final InterviewStageRepository interviewStageRepository;
    private final CandidateRepository candidateRepository;
    private final CandidateMapper candidateMapper;
    private static final Logger logger = LoggerFactory.getLogger(InterviewProcessService.class);



    public InterviewProcessService(
            Validator validator,
            InterviewProcessMapper interviewProcessMapper,
            InterviewProcessRepository interviewProcessRepository,
            CandidateRepository candidateRepository,
            CandidateMapper candidateMapper,
            InterviewStageRepository interviewStageRepository,
            InterviewStageMapper interviewStageMapper)
    {
        this.validator = validator;
        this.interviewProcessMapper = interviewProcessMapper;
        this.interviewProcessRepository = interviewProcessRepository;
        this.candidateMapper = candidateMapper;
        this.candidateRepository = candidateRepository;
        this.interviewStageRepository = interviewStageRepository;
        this.interviewStageMapper = interviewStageMapper;
    }

    public InterviewProcessDTO createInterviewProcess(InterviewProcessDTO interviewProcessDTO) {
        logger.debug("Validating interview process for creation: {}", interviewProcessDTO);

        if (interviewProcessDTO == null) {
            throw new IllegalArgumentException("InterviewProcess cannot be null");
        }

        interviewProcessDTO.setCreatedAt(LocalDateTime.now());
        interviewProcessDTO.setStatus(NOT_STARTED_YET);

        Set<ConstraintViolation<InterviewProcessDTO>> violations = validator.validate(interviewProcessDTO);
        if (!violations.isEmpty()) {
            logger.error("Validation errors: {}", getValidationErrors(violations));
            throw new IllegalArgumentException("Validation errors: " + getValidationErrors(violations));
        }


        InterviewProcess interviewProcess = interviewProcessMapper.toEntity(interviewProcessDTO);

        InterviewProcess savedInterviewProcess = interviewProcessRepository.save(interviewProcess);
        logger.info("Interview process created with ID: {}", savedInterviewProcess.getId());
        return interviewProcessMapper.toDTO(savedInterviewProcess);
    }

    public Page<InterviewProcessDTO> getAllInterviewProcesses(int limit, int offset) {
        logger.debug("Fetching all interview processes with limit: {}, offset: {}", limit, offset);

        if (limit < 1 || offset < 0) {
            throw new IllegalArgumentException("Limit must be greater than 0 and offset must be non-negative.");
        }

        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<InterviewProcess> interviewProcesses = interviewProcessRepository.findAll(pageable);
        return interviewProcesses.map(interviewProcessMapper::toDTO);
    }

    public InterviewProcessDTO getInterviewProcessById(UUID id) {
        logger.debug("Fetching interview process with ID: {}", id);

        InterviewProcess interviewProcess = interviewProcessRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("InterviewProcess not found"));

        return interviewProcessMapper.toDTO(interviewProcess);
    }

    public InterviewProcessDTO updateInterviewProcess(UUID id, InterviewProcessDTO interviewProcessDTO) {
        logger.debug("Updating interview process with ID: {}", id);

        if (interviewProcessDTO == null) {
            throw new IllegalArgumentException("InterviewProcessDTO cannot be null");
        }

        return interviewProcessRepository.findById(id)
                .map(existingInterviewProcess -> {
                    if (interviewProcessDTO.getTitle() != null) {
                        Set<ConstraintViolation<InterviewProcessDTO>> violations = validator.validateProperty(interviewProcessDTO, "title");
                        if (!violations.isEmpty()) {
                            logger.error("Validation errors: {}", getValidationErrors(violations));
                            throw new IllegalArgumentException("Validation errors: " + getValidationErrors(violations));
                        }
                        existingInterviewProcess.setTitle(interviewProcessDTO.getTitle());
                    }

                    if (interviewProcessDTO.getStatus() != null) {
                        existingInterviewProcess.setStatus(interviewProcessDTO.getStatus());
                    }


                    return interviewProcessRepository.save(existingInterviewProcess);
                })
                .map(interviewProcessMapper::toDTO)
                .orElseThrow(() -> {
                    logger.error("InterviewProcess not found with ID: {}", id);
                    return new IllegalArgumentException("InterviewProcess not found");
                });
    }

    public void deleteInterviewProcess(UUID id) {
        logger.debug("Deleting interview process with ID: {}", id);

        if (!interviewProcessRepository.existsById(id)) {
            logger.error("InterviewProcess not found with ID: {}", id);
            throw new IllegalArgumentException("InterviewProcess not found");
        }

        interviewProcessRepository.deleteById(id);
        logger.info("Interview process deleted with ID: {}", id);
    }


    public List<StageDetailsDTO> getStagesDetails(UUID processId) {
        logger.debug("Fetching stage details for process ID: {}", processId);

        if (processId == null) {
            logger.error("Process ID cannot be null");
            throw new IllegalArgumentException("Process ID cannot be null");
        }

        // Check if process ID exists
        if (!interviewProcessRepository.existsById(processId)) {
            logger.warn("Process with ID: {} not found", processId);
            throw new IllegalArgumentException("Process with the given ID does not exist");
        }

        try {
            List<StageDetailsDTO> stageDetails = interviewStageRepository.findStageDetailsByProcessId(processId);

            if (stageDetails.isEmpty()) {
                logger.warn("No stages found for process ID: {}", processId);
            } else {
                logger.info("Retrieved {} stages for process ID: {}", stageDetails.size(), processId);
            }

            return stageDetails;

        } catch (Exception e) {
            logger.error("Error occurred while fetching stage details for process ID: {}", processId, e);
            throw new RuntimeException("Error occurred while fetching stage details: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void addCandidateToProcess(UUID candidateId, UUID processId) {
        // Fetch Candidate and InterviewProcess entities by their IDs
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + candidateId));

        InterviewProcess interviewProcess = interviewProcessRepository.findById(processId)
                .orElseThrow(() -> new RuntimeException("InterviewProcess not found with id: " + processId));

        // Add Candidate to InterviewProcess
        interviewProcess.getCandidates().add(candidate);

        // Add InterviewProcess to Candidate
        candidate.getInterviewProcesses().add(interviewProcess);

        // Check for the first stage in the process

        Pageable pageable = PageRequest.of(0, 1);  // Limit to the first result
        List<InterviewStage> stages = interviewStageRepository.findStagesByInterviewProcessId(processId, pageable);
        InterviewStage firstStage = stages.isEmpty() ? null : stages.get(0);

        if (firstStage != null) {
            // Add Candidate to the first stage
            firstStage.getCandidates().add(candidate);
            candidate.getInterviewStages().add(firstStage);
            interviewStageRepository.save(firstStage); // Persist the change in the stage
        }

        // Save both entities to persist the relationship
        candidateRepository.save(candidate);
        interviewProcessRepository.save(interviewProcess);
    }

    public InterviewStageDTO addStageToProcess(InterviewStageDTO interviewStageDTO) {
        // Validate that the process exists using the processId from the DTO
        InterviewProcess interviewProcess = interviewProcessRepository.findById(interviewStageDTO.getInterviewProcessId())
                .orElseThrow(() -> new IllegalArgumentException("InterviewProcess with ID " + interviewStageDTO.getInterviewProcessId() + " not found"));

        // Calculate the next stage order for the process
        int nextStageOrder = interviewStageRepository
                .findMaxStageOrderByInterviewProcessId(interviewProcess.getId())
                .orElse(0) + 1;

        // Use the mapper to convert DTO to Entity
        InterviewStage interviewStage = interviewStageMapper.interviewStageDTOToInterviewStage(interviewStageDTO);

        // If this is the first stage, add all candidates of the process to the stage
        if (nextStageOrder == 1) {
            interviewStage.setCandidates(new HashSet<>(interviewProcess.getCandidates()));
        }

        // Set the stage order and associate the stage with the process
        interviewStage.setStageOrder(nextStageOrder);
        interviewStage.setInterviewProcess(interviewProcess);

        // Save the InterviewStage to the database
        InterviewStage savedStage = interviewStageRepository.save(interviewStage);

        logger.info("Added new stage '{}' with order {} to Interview Process with ID {}",
                interviewStage.getName(), nextStageOrder, interviewStageDTO.getInterviewProcessId());

        // Return the saved stage as a DTO
        return interviewStageMapper.interviewStageToInterviewStageDTO(savedStage);
    }


}
