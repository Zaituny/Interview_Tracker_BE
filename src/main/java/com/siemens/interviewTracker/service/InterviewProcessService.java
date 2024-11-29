package com.siemens.interviewTracker.service;

import java.time.LocalDateTime;
import java.util.*;

import com.siemens.interviewTracker.dto.CandidateDTO;
import com.siemens.interviewTracker.dto.InterviewStageDTO;
import com.siemens.interviewTracker.entity.*;
import com.siemens.interviewTracker.exception.UserNotFoundException;
import com.siemens.interviewTracker.mapper.CandidateMapper;
import com.siemens.interviewTracker.mapper.InterviewStageMapper;
import com.siemens.interviewTracker.repository.CandidateRepository;
import com.siemens.interviewTracker.repository.CandidateStatusRepository;
import com.siemens.interviewTracker.repository.InterviewStageRepository;

import com.siemens.interviewTracker.dto.StageDetailsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Validator;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import jakarta.validation.ConstraintViolation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import com.siemens.interviewTracker.dto.InterviewProcessDTO;
import com.siemens.interviewTracker.mapper.InterviewProcessMapper;
import com.siemens.interviewTracker.repository.InterviewProcessRepository;

import static com.siemens.interviewTracker.entity.CandidateProcessStatus.IN_PROGRESS;
import static com.siemens.interviewTracker.entity.InterviewProcessStatus.*;
import static com.siemens.interviewTracker.utils.ValidationUtils.getValidationErrors;


@Service
@Transactional
public class InterviewProcessService {
    private final Validator validator;
    private final InterviewProcessMapper interviewProcessMapper;

    private final InterviewStageMapper interviewStageMapper;
    private final InterviewProcessRepository interviewProcessRepository;

    private final InterviewStageService interviewStageService;
    private final InterviewStageRepository interviewStageRepository;
    private final CandidateRepository candidateRepository;
    private final CandidateMapper candidateMapper;
    private final CandidateStatusRepository candidateStatusRepository;
    private static final Logger logger = LoggerFactory.getLogger(InterviewProcessService.class);



    public InterviewProcessService(
            Validator validator,
            InterviewProcessMapper interviewProcessMapper,
            InterviewProcessRepository interviewProcessRepository,
            CandidateRepository candidateRepository,
            CandidateMapper candidateMapper,
            InterviewStageService interviewStageService,
            InterviewStageRepository interviewStageRepository,
            InterviewStageMapper interviewStageMapper ,
            CandidateStatusRepository candidateStatusRepository)
    {
        this.validator = validator;
        this.interviewProcessMapper = interviewProcessMapper;
        this.interviewProcessRepository = interviewProcessRepository;
        this.candidateMapper = candidateMapper;
        this.candidateRepository = candidateRepository;
        this.interviewStageService = interviewStageService;
        this.interviewStageRepository = interviewStageRepository;
        this.interviewStageMapper = interviewStageMapper;
        this.candidateStatusRepository = candidateStatusRepository;
    }

    public InterviewProcessDTO createInterviewProcess(InterviewProcessDTO interviewProcessDTO) {
        logger.debug("Validating interview process for creation: {}", interviewProcessDTO);

        if (interviewProcessDTO == null) {
            throw new IllegalArgumentException("InterviewProcess cannot be null");
        }

        // Set additional fields
        interviewProcessDTO.setCreatedAt(LocalDateTime.now());
        interviewProcessDTO.setStatus(NOT_STARTED_YET);

        // Retrieve the user email from the security context
        String userEmail = getCurrentUserEmail();
        interviewProcessDTO.setCreatedBy(userEmail);

        logger.info("Creating interview process by user: {}", userEmail);

        // Validate the DTO
        Set<ConstraintViolation<InterviewProcessDTO>> violations = validator.validate(interviewProcessDTO);
        if (!violations.isEmpty()) {
            logger.error("Validation errors: {}", getValidationErrors(violations));
            throw new IllegalArgumentException("Validation errors: " + getValidationErrors(violations));
        }

        // Map to entity and save
        InterviewProcess interviewProcess = interviewProcessMapper.toEntity(interviewProcessDTO);
        InterviewProcess savedInterviewProcess = interviewProcessRepository.save(interviewProcess);

        logger.info("Interview process created with ID: {}", savedInterviewProcess.getId());
        return interviewProcessMapper.toDTO(savedInterviewProcess);
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        throw new RuntimeException("User email not found in the security context");
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
        InterviewProcess interviewProcess = interviewProcessRepository.findById(id).get();

        interviewProcessRepository.delete(interviewProcess);
        logger.info("Interview process deleted with ID: {}", id);
    }


    public List<StageDetailsDTO> getStagesDetails(UUID processId) {
        logger.debug("Fetching stage details for process ID: {}", processId);

        if (processId == null) {
            logger.error("Process ID cannot be null");
            throw new IllegalArgumentException("Process ID cannot be null");
        }

        if (!interviewProcessRepository.existsById(processId)) {
            logger.warn("Process with ID: {} not found", processId);
            throw new IllegalArgumentException("Process with the given ID does not exist");
        }

        try {
            List<StageDetailsDTO> stageDetails = interviewStageRepository.findStageDetailsByProcessId(processId);

            if (stageDetails.isEmpty()) {
                logger.warn("No stages found for process ID: {}", processId);
                return stageDetails;
            }

            stageDetails.forEach(dto -> {
                long activeCount = interviewStageService.getActiveCandidateCountInStage(dto.getStageId());
                dto.setNumberOfCandidates(activeCount);
            });

            logger.info("Retrieved {} stages for process ID: {}", stageDetails.size(), processId);
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
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found with id: " + candidateId));

        InterviewProcess interviewProcess = interviewProcessRepository.findById(processId)
                .orElseThrow(() -> new IllegalArgumentException("InterviewProcess not found with id: " + processId));

        // Create and set CandidateStatus
        CandidateStatus candidateStatus = new CandidateStatus();
        candidateStatus.setCandidate(candidate);
        candidateStatus.setInterviewProcess(interviewProcess);
        candidateStatus.setStatus(IN_PROGRESS);

        candidate.getCandidateStatuses().add(candidateStatus);
        interviewProcess.getCandidateStatuses().add(candidateStatus);

        candidateStatusRepository.save(candidateStatus); // Save the CandidateStatus
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

    @Transactional
    public void addBulkCandidatesToProcess(UUID processId, List<UUID> candidateIds) {
        logger.debug("Adding bulk candidates to process ID: {}", processId);

        // Fetch the InterviewProcess
        InterviewProcess interviewProcess = interviewProcessRepository.findById(processId)
                .orElseThrow(() -> new IllegalArgumentException("InterviewProcess not found with id: " + processId));

        // Fetch all candidates by their IDs
        List<Candidate> candidates = candidateRepository.findAllById(candidateIds);
        if (candidates.size() != candidateIds.size()) {
            logger.error("Some candidates not found. Expected: {}, Found: {}", candidateIds.size(), candidates.size());
            throw new UserNotFoundException("Some candidates not found or invalid candidate IDs provided.");
        }


        // Get the first stage of the process, if it exists
        Pageable pageable = PageRequest.of(0, 1);  // Fetch the first stage only
        List<InterviewStage> stages = interviewStageRepository.findStagesByInterviewProcessId(processId, pageable);
        InterviewStage firstStage = stages.isEmpty() ? null : stages.get(0);

        candidates.forEach(candidate -> {
            // Create and set CandidateStatus for each candidate
            CandidateStatus candidateStatus = new CandidateStatus();
            candidateStatus.setCandidate(candidate);
            candidateStatus.setInterviewProcess(interviewProcess);
            candidateStatus.setStatus(IN_PROGRESS);
            candidate.getCandidateStatuses().add(candidateStatus);
            if (firstStage != null) {
                candidateStatus.setCurrentStage(firstStage);

            }
            candidate.getCandidateStatuses().add(candidateStatus);
            interviewProcess.getCandidateStatuses().add(candidateStatus);
            candidateStatusRepository.save(candidateStatus);
        });



        if (firstStage != null) {
            candidates.forEach(candidate -> {
                firstStage.getCandidates().add(candidate);
                candidate.getInterviewStages().add(firstStage);

            });
            interviewStageRepository.save(firstStage); // Persist changes in the stage
        }
        // Add candidates to the process
        interviewProcess.getCandidates().addAll(candidates);
        // Persist changes in the process
        interviewProcessRepository.save(interviewProcess);

        logger.info("Bulk candidates added to process ID: {}", processId);
    }

    public InterviewStageDTO addStageToProcess(InterviewStageDTO interviewStageDTO , UUID processId) {
        // Validate that the process exists using the processId from the DTO
        InterviewProcess interviewProcess = interviewProcessRepository.findById(processId)
                .orElseThrow(() -> new IllegalArgumentException("InterviewProcess with ID " + processId + " not found"));

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
        if (nextStageOrder == 1) {

            // Update the current stage ID in CandidateStatus for each candidate
            for (Candidate candidate : interviewProcess.getCandidates()) {
                CandidateStatus status = candidateStatusRepository
                        .findByCandidateAndInterviewProcess(candidate, interviewProcess)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "CandidateStatus not found for candidate ID " + candidate.getId() + " in process ID " + interviewProcess.getId()));

                // Set the current stage to the newly created stage
                status.setCurrentStage(savedStage);

                // Save the updated status
                candidateStatusRepository.save(status);
            }
        }
        interviewProcess.setStatus(ACTIVE);
        logger.info("Added new stage '{}' with order {} to Interview Process with ID {}",
                interviewStage.getName(), nextStageOrder, interviewStageDTO.getInterviewProcessId());

        // Return the saved stage as a DTO
        return interviewStageMapper.interviewStageToInterviewStageDTO(savedStage);
    }

    @Transactional
    public void addCandidateToNextStage(UUID candidateId, UUID currentStageId , UUID processId) {
        logger.debug("Adding candidate {} to the next stage of process associated with stage {}", candidateId, currentStageId);

        // Fetch the current stage
        InterviewStage currentStage = interviewStageRepository.findById(currentStageId)
                .orElseThrow(() -> new IllegalArgumentException("Stage not found with id: " + currentStageId));

        // Fetch the process associated with the stage
        InterviewProcess process = interviewProcessRepository.findById(processId)
                .orElseThrow(() -> new IllegalArgumentException("process not found with id: " + processId));

        // Fetch the candidate
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found with id: " + candidateId));

        // Ensure the candidate is part of the process
        if (!process.getCandidates().contains(candidate)) {
            throw new IllegalArgumentException("Candidate is not part of the process with id: " + processId);
        }

        // Find the next stage in the process
        int nextStageOrder = currentStage.getStageOrder() + 1;
        InterviewStage nextStage = interviewStageRepository.findByInterviewProcessIdAndStageOrder(processId, nextStageOrder)
                .orElseThrow(() -> new IllegalArgumentException("No next stage found for process with id: " + processId));

        // Fetch or create the CandidateStatus for this candidate and process
        CandidateStatus candidateStatus = candidate.getCandidateStatuses().stream()
                .filter(status -> status.getInterviewProcess().getId().equals(processId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No status found for candidate in process with id: " + processId));

        // Update the candidate's current stage in the process
        candidateStatus.setCurrentStage(nextStage);

        // Update the candidate's status (if applicable)
        candidateStatus.setStatus(IN_PROGRESS);

        // Add the candidate to the next stage
        nextStage.getCandidates().add(candidate);

        // Persist the changes
        candidateStatusRepository.save(candidateStatus);
        interviewStageRepository.save(nextStage);

        logger.info("Candidate {} added to stage {} in process {}", candidateId, nextStage.getStageOrder(), processId);
    }

    @Transactional
    public void rejectCandidate(UUID interviewProcessId, UUID candidateId) {
        logger.debug("Rejecting candidate with ID: {} for process ID: {}", candidateId, interviewProcessId);

        InterviewProcess interviewProcess = interviewProcessRepository.findById(interviewProcessId)
                .orElseThrow(() -> new IllegalArgumentException("InterviewProcess not found with id: " + interviewProcessId));

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found with id: " + candidateId));

        if (!interviewProcess.getCandidates().contains(candidate)) {
            logger.error("Candidate with ID: {} is not part of the process with ID: {}", candidateId, interviewProcessId);
            throw new IllegalArgumentException("Candidate is not part of the given process");
        }

        CandidateStatus candidateStatus = interviewProcess.getCandidateStatuses()
                .stream()
                .filter(status -> status.getCandidate().equals(candidate))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Candidate status not found for the given process"));

        candidateStatus.setStatus(CandidateProcessStatus.REJECTED);

        interviewProcessRepository.save(interviewProcess);
        logger.info("Candidate with ID: {} has been rejected for process ID: {}", candidateId, interviewProcessId);
    }

    @Transactional
    public void rejectCandidates(UUID interviewProcessId, List<UUID> candidateIds) {
        logger.debug("Rejecting candidates for process ID: {}. Candidates: {}", interviewProcessId, candidateIds);

        InterviewProcess interviewProcess = interviewProcessRepository.findById(interviewProcessId)
                .orElseThrow(() -> new IllegalArgumentException("InterviewProcess not found with id: " + interviewProcessId));

        List<CandidateStatus> candidateStatuses = candidateRepository.findAllById(candidateIds)
                .stream()
                .map(candidate -> {
                    return interviewProcess.getCandidateStatuses()
                            .stream()
                            .filter(status -> status.getCandidate().equals(candidate))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Candidate with id " + candidate.getId() + " is not part of the process"));
                })
                .toList();

        candidateStatuses.forEach(candidateStatus -> candidateStatus.setStatus(CandidateProcessStatus.REJECTED));

        interviewProcessRepository.save(interviewProcess);
        logger.info("{} candidates rejected for process ID: {}", candidateIds.size(), interviewProcessId);
    }

    @Transactional
    public InterviewStageDTO getCandidateCurrentInterviewStage(UUID interviewProcessId, UUID candidateId) {
        logger.debug("Fetching current interview stage for candidate ID: {} in process ID: {}", candidateId, interviewProcessId);

        InterviewProcess interviewProcess = interviewProcessRepository.findById(interviewProcessId)
                .orElseThrow(() -> new IllegalArgumentException("InterviewProcess not found with id: " + interviewProcessId));

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found with id: " + candidateId));

        if (interviewProcess.getInterviewStages().isEmpty()) {
            throw new IllegalStateException("No interview stages found for the given interview process");
        }

        InterviewStage interviewStage =  interviewProcess.getInterviewStages()
                .stream()
                .filter(stage -> stage.getCandidates().contains(candidate))
                .max(Comparator.comparingInt(InterviewStage::getStageOrder))
                .orElseThrow(() -> new IllegalArgumentException("No interview stage found for the given candidate in the process"));

        return interviewStageMapper.interviewStageToInterviewStageDTO(interviewStage);
    }

    @Transactional
    public void acceptCandidate(UUID interviewProcessId, UUID candidateId) {
        logger.debug("Accepting candidate with the ID {} for the interview process with the ID {}.", candidateId, interviewProcessId);

        InterviewProcess interviewProcess = interviewProcessRepository.findById(interviewProcessId)
                .orElseThrow(() -> new IllegalArgumentException("Interview process not found with ID: " + interviewProcessId));

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found with ID: " + candidateId));

        if (!interviewProcess.getCandidates().contains(candidate)) {
            logger.error("Candidate with ID: {} is not part of the process with ID: {}", candidateId, interviewProcessId);
            throw new IllegalArgumentException("Candidate is not part of the given process");
        }

        CandidateStatus candidateStatus = interviewProcess.getCandidateStatuses()
                .stream()
                .filter(status -> status.getCandidate().equals(candidate))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Candidate status not found for process ID: " + interviewProcessId));

        InterviewStage lastStage = interviewProcess.getInterviewStages()
                .stream()
                .max(Comparator.comparingInt(InterviewStage::getStageOrder))
                .orElseThrow(() -> new IllegalArgumentException("No stages defined for the interview process"));

        if (!candidateStatus.getCurrentStage().equals(lastStage)) {
            logger.error("Candidate with ID: {} is not in the last stage of the process with ID: {}", candidateId, interviewProcessId);
            throw new IllegalArgumentException("Candidate is not at the last stage of the process");
        }

        candidateStatus.setStatus(CandidateProcessStatus.ACCEPTED);

        interviewProcessRepository.save(interviewProcess);
        logger.info("Candidate {} is accepted for the interview process ID {}", candidateId, interviewProcessId);
    }

    public void completeInterviewProcess(UUID interviewProcessId){
        logger.info("Fetching interview process with ID: {}", interviewProcessId);
        InterviewProcess interviewProcess = interviewProcessRepository.findById(interviewProcessId)
                .orElseThrow(() -> new IllegalArgumentException("InterviewProcess not found"));

        interviewProcess.setStatus(COMPLETED);
    }
}
