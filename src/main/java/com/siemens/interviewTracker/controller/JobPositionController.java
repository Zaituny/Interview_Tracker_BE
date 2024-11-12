package com.siemens.interviewTracker.controller;

import com.siemens.interviewTracker.dto.JobPositionDTO;
import com.siemens.interviewTracker.service.JobPositionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v0/job-position")
public class JobPositionController {
    private final JobPositionService jobPositionService;
    private static final Logger logger = LoggerFactory.getLogger(JobPositionController.class);
    public JobPositionController(JobPositionService jobPositionService) {
        this.jobPositionService = jobPositionService;
    }

    @PostMapping
    public ResponseEntity<JobPositionDTO> createJobPosition(@RequestBody JobPositionDTO jobPositionDTO) {
        logger.info("creating job position with title");
        JobPositionDTO createdJobPositionDTO = jobPositionService.createJobPosition(jobPositionDTO);
        logger.info("job position created");
        return ResponseEntity.status(HttpStatus.CREATED).body(createdJobPositionDTO);
    }

    @GetMapping
    public ResponseEntity<List<JobPositionDTO>> getAllJobPositions(@RequestParam(defaultValue = "10") int limit,
                                                                   @RequestParam(defaultValue = "0") int offset) {
        logger.info("getting all job positions with limit: {}, offset: {}", limit, offset);
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100.");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be non-negative.");
        }
        List<JobPositionDTO>  jobPositions = jobPositionService.getAllJobPositions(limit , offset).getContent();
        return ResponseEntity.ok(jobPositions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPositionDTO> getJobPosition(@PathVariable UUID id) {
        logger.info("getting job position with id: {}", id);
        JobPositionDTO jobPositionDTO = jobPositionService.getJobPositionById(id);
        return ResponseEntity.ok(jobPositionDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<JobPositionDTO> updateJobPosition(@PathVariable UUID id, @RequestBody JobPositionDTO jobPositionDTO) {
        logger.info("updating job position with id: {}", id);
        JobPositionDTO newJobPositionDTO = jobPositionService.updateJobPosition(id, jobPositionDTO);
        return ResponseEntity.ok(newJobPositionDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<JobPositionDTO> deleteJobPosition(@PathVariable UUID id) {
        logger.info("deleting job position with id: {}", id);
        jobPositionService.deleteJobPosition(id);
        return ResponseEntity.noContent().build();
    }
}
