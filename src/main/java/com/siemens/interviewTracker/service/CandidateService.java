package com.siemens.interviewTracker.service;

import com.siemens.interviewTracker.dto.*;
import com.siemens.interviewTracker.entity.Candidate;
import com.siemens.interviewTracker.entity.CandidateStatus;
import com.siemens.interviewTracker.entity.InterviewProcess;
import com.siemens.interviewTracker.entity.InterviewStage;
import com.siemens.interviewTracker.exception.CandidateDeletionException;
import com.siemens.interviewTracker.exception.DuplicateFieldException;
import com.siemens.interviewTracker.mapper.CandidateMapper;
import com.siemens.interviewTracker.repository.CandidateRepository;
import com.siemens.interviewTracker.repository.InterviewProcessRepository;
import com.siemens.interviewTracker.repository.InterviewStageRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.siemens.interviewTracker.utils.ValidationUtils.getValidationErrors;

@Service
@Transactional
public class CandidateService {

    private final Validator validator;
    private final CandidateMapper candidateMapper;
    private final CandidateRepository candidateRepository;
    private final InterviewProcessRepository interviewProcessRepository;
    private static final Logger logger = LoggerFactory.getLogger(CandidateService.class);
    private final InterviewStageRepository interviewStageRepository;

    public CandidateService(Validator validator,
                            CandidateMapper candidateMapper,
                            CandidateRepository candidateRepository,
                            InterviewProcessRepository interviewProcessRepository, InterviewStageRepository interviewStageRepository) {
        this.validator = validator;
        this.candidateMapper = candidateMapper;
        this.candidateRepository = candidateRepository;
        this.interviewProcessRepository = interviewProcessRepository;
        this.interviewStageRepository = interviewStageRepository;
    }

    public CandidateDTO createCandidate(CandidateDTO candidateDTO) {
        logger.debug("Validating candidate for creation: {}", candidateDTO);

        if (candidateDTO == null) {
            throw new IllegalArgumentException("Candidate cannot be null");
        }

        // Check for duplicate email
        if (candidateRepository.findByEmail(candidateDTO.getEmail()).isPresent()) {
            throw new DuplicateFieldException("email", candidateDTO.getEmail());
        }

        // Check for duplicate phone number (if phone number is provided)
        if (candidateDTO.getPhone() != null && candidateRepository.findByPhone(candidateDTO.getPhone()).isPresent()) {
            throw new DuplicateFieldException("phone", candidateDTO.getPhone());
        }

        // Validate candidate DTO
        Set<ConstraintViolation<CandidateDTO>> violations = validator.validate(candidateDTO);
        if (!violations.isEmpty()) {
            logger.error("Validation errors: {}", getValidationErrors(violations));
            String validationErrors = getValidationErrors(violations);
            throw new IllegalArgumentException("Validation errors: " + validationErrors);
        }

        // Convert DTO to entity and save
        Candidate candidate = candidateMapper.toEntity(candidateDTO);
        candidateRepository.save(candidate);

        return candidateMapper.toDTO(candidate);
    }


    public Page<CandidateDTO> getAllCandidates(int limit, int offset) {
        logger.debug("Fetching all candidates with limit: {}, offset: {}", limit, offset);
        if (limit < 1 || offset < 0) {
            throw new IllegalArgumentException("Limit must be greater than 0 and offset must be non-negative.");
        }
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Candidate> candidates = candidateRepository.findAll(pageable);
        return candidates.map(candidateMapper::toDTO);
    }

    public CandidateDTO getCandidateById(UUID id) {
        logger.info("Fetching candidate with ID: {}", id);
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));
        return candidateMapper.toDTO(candidate);
    }

    public CandidateProfileDTO getCandidateWithProcesses(UUID candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

        return candidateMapper.toCandidateWithProcessesDTO(candidate);
    }

    public CandidateDTO updateCandidate(UUID id, CandidateDTO candidateDTO) {
        logger.debug("Updating candidate with ID: {}", id);
        if (candidateDTO == null) {
            throw new IllegalArgumentException("Candidate cannot be null");
        }

        return candidateRepository.findById(id)
                .map(existingCandidate -> {
                    if (candidateDTO.getEmail() != null) {
                        Set<ConstraintViolation<CandidateDTO>> violations = validator.validateProperty(candidateDTO, "email");
                        if (!violations.isEmpty()) {
                            logger.error("Validation errors: {}", getValidationErrors(violations));
                            throw new IllegalArgumentException("Validation errors: " + getValidationErrors(violations));
                        }
                        existingCandidate.setEmail(candidateDTO.getEmail());
                    }
                    if (candidateDTO.getName() != null) {
                        Set<ConstraintViolation<CandidateDTO>> violations = validator.validateProperty(candidateDTO, "name");
                        if (!violations.isEmpty()) {
                            logger.error("Validation errors: {}", getValidationErrors(violations));
                            throw new IllegalArgumentException("Validation errors: " + getValidationErrors(violations));
                        }
                        existingCandidate.setName(candidateDTO.getName());
                    }
                    if (candidateDTO.getPhone() != null && !candidateDTO.getPhone().isEmpty()) {
                        existingCandidate.setPhone(candidateDTO.getPhone());
                    }
                    return candidateRepository.save(existingCandidate);
                })
                .map(candidateMapper::toDTO)
                .orElseThrow(() -> {
                    logger.error("Candidate not found with ID: {}", id);
                    return new IllegalArgumentException("Candidate not found");
                });
    }

    public void deleteCandidate(UUID id) {
        logger.info("Deleting candidate with ID: {}", id);

        // Fetch candidate to check associations
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Candidate with ID " + id + " not found"));

        // Prepare a map to hold association details if any
        Map<String, Object> errorDetails = new HashMap<>();

        // Check for interview process associations
        if (!candidate.getInterviewProcesses().isEmpty()) {
            errorDetails.put("interviewProcesses", candidate.getInterviewProcesses().size());
        }

        // Check for interview stage associations
        if (!candidate.getInterviewStages().isEmpty()) {
            errorDetails.put("interviewStages", candidate.getInterviewStages().size());
        }

        // If there are associations, throw the custom exception
        if (!errorDetails.isEmpty()) {
            errorDetails.put("message", "Candidate cannot be deleted due to existing associations");
            errorDetails.put("candidateId", id);
            logger.warn("Candidate deletion blocked due to associations: {}", errorDetails);

            throw new CandidateDeletionException(errorDetails);
        }

        // Proceed to delete candidate
        candidateRepository.deleteById(id);
        logger.info("Candidate with ID: {} has been successfully deleted", id);
    }

    public Page<CandidateInProcessDTO> getCandidatesInInterviewProcess(UUID interviewProcessId, int limit, int offset) {
        InterviewProcess interviewProcess = interviewProcessRepository.findById(interviewProcessId)
                .orElseThrow(() -> new EntityNotFoundException("Interview process with ID " + interviewProcessId + " not found"));

        Pageable pageable = PageRequest.of(offset / limit, limit);

        // Retrieve a page of candidates for the given interview process
        Page<Candidate> candidatesPage = candidateRepository.findByInterviewProcessesContaining(interviewProcess, pageable);

        // Map the candidates to CandidateInProcessDTO with custom logic
        List<CandidateInProcessDTO> candidateInProcessDTOs = candidatesPage.getContent().stream()
                .map(candidate -> {
                    CandidateStatus status = candidate.getCandidateStatuses().stream()
                            .filter(s -> s.getInterviewProcess().getId().equals(interviewProcess.getId()))
                            .findFirst()
                            .orElse(null);

                    String statusInProcess = status != null ? status.getStatus().name() : "Not Assigned";
                    String currentStageInProcess = (status != null && status.getCurrentStage() != null)
                            ? status.getCurrentStage().getName()
                            : "Not Assigned";

                    return candidateMapper.toCandidateInProcessDTO(
                            candidateMapper.toDTO(candidate),
                            statusInProcess,
                            currentStageInProcess
                    );
                })
                .collect(Collectors.toList());

        // Return a Page object with pagination details
        return new PageImpl<>(candidateInProcessDTOs, pageable, candidatesPage.getTotalElements());
    }

    public Page<CandidateDTO> getCandidatesInInterviewStage(UUID interviewStageId, int limit, int offset) {
        logger.debug("Fetching candidates for interview stage with ID: {}. Limit: {}, Offset: {}", interviewStageId, limit, offset);

        if (limit < 1 || offset < 0) {
            throw new IllegalArgumentException("Limit and offset must be greater than 0");
        }

        if (interviewStageId == null) {
            throw new IllegalArgumentException("Interview stage id cannot be null");
        }

        InterviewStage interviewStage = interviewStageRepository.findById(interviewStageId)
                .orElseThrow(() -> new EntityNotFoundException("Interview stage with ID " + interviewStageId + " not found"));

        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Candidate> candidates = candidateRepository.findByInterviewStagesContaining(interviewStage, pageable);

        return  candidates.map(candidateMapper::toDTO);
    }
}
