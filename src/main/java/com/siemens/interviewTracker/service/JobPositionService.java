package com.siemens.interviewTracker.service;

import com.siemens.interviewTracker.dto.JobPositionDTO;
import com.siemens.interviewTracker.entity.JobPosition;
import com.siemens.interviewTracker.mapper.JobPositionMapper;
import com.siemens.interviewTracker.repository.JobPositionRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

import static com.siemens.interviewTracker.utils.ValidationUtils.getValidationErrors;

@Service
public class JobPositionService {

    private final JobPositionMapper jobPositionMapper;
    private final JobPositionRepository jobPositionRepository;
    private final Validator validator;
    private static final Logger logger = LoggerFactory.getLogger(JobPositionService.class);

    public JobPositionService(JobPositionMapper jobPositionMapper, JobPositionRepository jobPositionRepository , Validator validator) {
        this.jobPositionMapper = jobPositionMapper;
        this.jobPositionRepository = jobPositionRepository;
        this.validator = validator;
    }

    public JobPositionDTO createJobPosition(JobPositionDTO JobPositionDTO) {
        logger.debug("Validating Job Position for creation: {}", JobPositionDTO);
        if (JobPositionDTO == null) {
            throw new IllegalArgumentException("Job position cannot be null");
        }

        Set<ConstraintViolation<JobPositionDTO>> violations = validator.validate(JobPositionDTO);
        if (!violations.isEmpty()) {
            logger.error("Validation errors: {}", getValidationErrors(violations));
            String validationErrors = getValidationErrors(violations);
            throw new IllegalArgumentException("Validation errors: " + validationErrors);
        }
        JobPosition jobPosition = jobPositionMapper.toJobPosition(JobPositionDTO);
        JobPosition savedJobPosition = jobPositionRepository.save(jobPosition);
        logger.info("Saved Job Position with id : {}" , savedJobPosition.getId());
        return jobPositionMapper.toJobPositionDTO(savedJobPosition);
    }

    public Page<JobPositionDTO> getAllJobPositions(int limit, int offset){
        logger.debug("Fetching all job positions with limit: {}, offset: {}", limit, offset);
        if (limit < 1 || offset < 0) {
            throw new IllegalArgumentException("Limit must be greater than 0 and offset must be non-negative.");
        }
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<JobPosition> jobPositions = jobPositionRepository.findAll(pageable);
        return jobPositions.map(jobPositionMapper::toJobPositionDTO);
    }

    public JobPositionDTO getJobPositionById(UUID id){

        logger.debug("Fetching job position with id: {}", id);
        JobPosition jobPosition = jobPositionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job Position not found"));
        return jobPositionMapper.toJobPositionDTO(jobPosition);
    }

    public JobPositionDTO updateJobPosition(UUID id, JobPositionDTO jobPositionDTO) {
        logger.debug("Updating job position with ID: {}", id);
        if (jobPositionDTO == null) {
            throw new IllegalArgumentException("Job Position cannot be null");
        }

        return jobPositionRepository.findById(id)
                .map(existingJobPosition -> {
                    if (jobPositionDTO.getTitle() != null) {
                        Set<ConstraintViolation<JobPositionDTO>> violations = validator.validateProperty(jobPositionDTO, "title");
                        if (!violations.isEmpty()) {
                            logger.error("Validation errors: {}", getValidationErrors(violations));
                            throw new IllegalArgumentException("Validation errors: " + getValidationErrors(violations));
                        }
                        existingJobPosition.setTitle(jobPositionDTO.getTitle());
                    }
                    if (jobPositionDTO.getDescription() != null) {
                        Set<ConstraintViolation<JobPositionDTO>> violations = validator.validateProperty(jobPositionDTO, "description");
                        if (!violations.isEmpty()) {
                            logger.error("Validation errors: {}", getValidationErrors(violations));
                            throw new IllegalArgumentException("Validation errors: " + getValidationErrors(violations));
                        }
                        existingJobPosition.setDescription(jobPositionDTO.getDescription());
                    }
                    if (jobPositionDTO.getStatus() != null) {
                        Set<ConstraintViolation<JobPositionDTO>> violations = validator.validateProperty(jobPositionDTO, "status");
                        if (!violations.isEmpty()) {
                            logger.error("Validation errors: {}", getValidationErrors(violations));
                            throw new IllegalArgumentException("Validation errors: " + getValidationErrors(violations));
                        }
                        existingJobPosition.setStatus(jobPositionDTO.getStatus());
                    }
                    if (jobPositionDTO.getRequirements() != null) {
                        Set<ConstraintViolation<JobPositionDTO>> violations = validator.validateProperty(jobPositionDTO, "requirements");
                        if (!violations.isEmpty()) {
                            logger.error("Validation errors: {}", getValidationErrors(violations));
                            throw new IllegalArgumentException("Validation errors: " + getValidationErrors(violations));
                        }
                        existingJobPosition.setRequirements(jobPositionDTO.getRequirements());
                    }
                    if (jobPositionDTO.getResponsibilities() != null) {
                        Set<ConstraintViolation<JobPositionDTO>> violations = validator.validateProperty(jobPositionDTO, "responsibilities");
                        if (!violations.isEmpty()) {
                            logger.error("Validation errors: {}", getValidationErrors(violations));
                            throw new IllegalArgumentException("Validation errors: " + getValidationErrors(violations));
                        }
                        existingJobPosition.setResponsibilities(jobPositionDTO.getResponsibilities());
                    }

                    return jobPositionRepository.save(existingJobPosition);
                })
                .map(jobPositionMapper::toJobPositionDTO)
                .orElseThrow(() -> {
                    logger.error("Job Position not found with ID: {}", id);
                    return new IllegalArgumentException("Job Position not found");
                });
    }

    public void deleteJobPosition(UUID id) {
        logger.debug("Deleting job position with ID: {}", id);
        if (!jobPositionRepository.existsById(id)) {
            logger.error("Job position not found with ID: {}", id);
            throw new IllegalArgumentException("Job Position not found");
        }
        jobPositionRepository.deleteById(id);
        logger.info("Job Position deleted with ID: {}", id);

    }
}
