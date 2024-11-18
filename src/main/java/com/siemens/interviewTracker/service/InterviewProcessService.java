package com.siemens.interviewTracker.service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.siemens.interviewTracker.dto.CandidateDTO;
import com.siemens.interviewTracker.entity.Candidate;
import com.siemens.interviewTracker.mapper.CandidateMapper;
import com.siemens.interviewTracker.repository.CandidateRepository;
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
    private final InterviewProcessRepository interviewProcessRepository;
    private final CandidateRepository candidateRepository;
    private final CandidateMapper candidateMapper;
    private static final Logger logger = LoggerFactory.getLogger(InterviewProcessService.class);



    public InterviewProcessService(
            Validator validator,
            InterviewProcessMapper interviewProcessMapper,
            InterviewProcessRepository interviewProcessRepository,
            CandidateRepository candidateRepository,
            CandidateMapper candidateMapper) {
        this.validator = validator;
        this.interviewProcessMapper = interviewProcessMapper;
        this.interviewProcessRepository = interviewProcessRepository;
        this.candidateMapper = candidateMapper;
        this.candidateRepository = candidateRepository;
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

    @Transactional
    public CandidateDTO createCandidateAndAddToProcess(CandidateDTO candidateDTO, UUID processId) {
        // Create new candidate from DTO
        Candidate candidate = candidateMapper.toEntity(candidateDTO);

        // Fetch InterviewProcess by processId
        InterviewProcess interviewProcess = interviewProcessRepository.findById(processId)
                .orElseThrow(() -> new IllegalArgumentException("Interview process not found"));

        // Add candidate to the interview process
        interviewProcess.addCandidate(candidate);

        // Persist the candidate and update the interview process
        candidateRepository.save(candidate);
        interviewProcessRepository.save(interviewProcess);

        // Convert saved candidate to DTO and return
        return candidateMapper.toDTO(candidate);
    }



}
