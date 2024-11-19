package com.siemens.interviewTracker.service;

import com.siemens.interviewTracker.dto.CandidateDTO;
import com.siemens.interviewTracker.dto.InterviewProcessDTO;
import com.siemens.interviewTracker.entity.Candidate;
import com.siemens.interviewTracker.entity.InterviewProcess;
import com.siemens.interviewTracker.exception.DuplicateFieldException;
import com.siemens.interviewTracker.mapper.CandidateMapper;
import com.siemens.interviewTracker.repository.CandidateRepository;
import com.siemens.interviewTracker.repository.InterviewProcessRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

import static com.siemens.interviewTracker.utils.PasswordValidator.validateRawPassword;
import static com.siemens.interviewTracker.utils.ValidationUtils.getValidationErrors;

@Service
@Transactional
public class CandidateService {

    private final Validator validator;
    private final CandidateMapper candidateMapper;
    private final CandidateRepository candidateRepository;
    private final InterviewProcessRepository interviewProcessRepository;
    private static final Logger logger = LoggerFactory.getLogger(CandidateService.class);

    public CandidateService(Validator validator,
                            CandidateMapper candidateMapper,
                            CandidateRepository candidateRepository,
                            InterviewProcessRepository interviewProcessRepository) {
        this.validator = validator;
        this.candidateMapper = candidateMapper;
        this.candidateRepository = candidateRepository;
        this.interviewProcessRepository = interviewProcessRepository;
    }

    public CandidateDTO createCandidate(CandidateDTO candidateDTO) {
        logger.debug("Validating candidate for creation: {}", candidateDTO);

        if (candidateDTO == null) {
            throw new IllegalArgumentException("Candidate cannot be null");
        }

        // Check for duplicate email
        if (candidateRepository.findByEmail(candidateDTO.getEmail()).isPresent()) {
            throw new DuplicateFieldException("A candidate with the provided email already exists");
        }

        // Check for duplicate phone number (if phone number is provided)
        if (candidateDTO.getPhone() != null && candidateRepository.findByPhone(candidateDTO.getPhone()).isPresent()) {
            throw new DuplicateFieldException("A candidate with the provided phone number already exists");
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
        candidateRepository.deleteById(id);
    }

    @Transactional
    public CandidateDTO createCandidateAndAddToProcess(CandidateDTO candidateDTO, UUID processId) {
        try {
            // Create new candidate from DTO
            Candidate candidate = candidateMapper.toEntity(candidateDTO);

            // Fetch InterviewProcess by processId
            InterviewProcess interviewProcess = interviewProcessRepository.findById(processId)
                    .orElseThrow(() -> new IllegalArgumentException("Interview process not found"));

            // Add interview process to the candidate
            candidate.getInterviewProcesses().add(interviewProcess);

            Candidate savedCandidate = candidateRepository.save(candidate);

            // Add candidate to the interview process
            interviewProcess.getCandidates().add(savedCandidate);
            interviewProcessRepository.save(interviewProcess);

            // Convert saved candidate to DTO and return
            return candidateMapper.toDTO(savedCandidate);

        } catch (DataIntegrityViolationException ex) {
            // Check for unique constraint violation
            if (ex.getMessage().contains("email")) {
                throw new DuplicateFieldException("Email already exists");
            } else if (ex.getMessage().contains("phone")) {
                throw new DuplicateFieldException("Phone number already exists");
            }
            throw ex; // Re-throw for other exceptions
        }
    }


}
