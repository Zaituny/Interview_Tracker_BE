package com.siemens.interviewTracker.controller;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.siemens.interviewTracker.dto.InterviewStageDTO;
import com.siemens.interviewTracker.dto.StageDetailsDTO;
import com.siemens.interviewTracker.exception.UserNotFoundException;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.siemens.interviewTracker.dto.InterviewProcessDTO;
import com.siemens.interviewTracker.service.InterviewProcessService;

@Validated
@RestController
@RequestMapping("api/v0/interview-process")
public class InterviewProcessController {

    private final InterviewProcessService interviewProcessService;
    private static final Logger logger = LoggerFactory.getLogger(InterviewProcessController.class);

    public InterviewProcessController(InterviewProcessService interviewProcessService) {
        this.interviewProcessService = interviewProcessService;
    }

    @PostMapping
    public ResponseEntity<InterviewProcessDTO> createInterviewProcess( @RequestBody InterviewProcessDTO interviewProcessDTO) {
        logger.info("Creating interview process with title: {}", interviewProcessDTO.getTitle());

        // Pass the DTO to the service; service handles setting `createdBy`
        InterviewProcessDTO createdInterviewProcess = interviewProcessService.createInterviewProcess(interviewProcessDTO);

        logger.info("Interview process created with ID: {}", createdInterviewProcess.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdInterviewProcess);
    }


    @GetMapping
    public ResponseEntity<List<InterviewProcessDTO>> getAllInterviewProcesses(@RequestParam(defaultValue = "10") int limit,
                                                                              @RequestParam(defaultValue = "0") int offset) {
        logger.info("Fetching interview processes with limit: {}, offset: {}", limit, offset);
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100.");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be non-negative.");
        }

        List<InterviewProcessDTO> interviewProcesses = interviewProcessService.getAllInterviewProcesses(limit, offset).getContent();
        return ResponseEntity.ok(interviewProcesses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterviewProcessDTO> getInterviewProcessById(@PathVariable UUID id) {
        logger.info("Fetching interview process with ID: {}", id);
        InterviewProcessDTO interviewProcess = interviewProcessService.getInterviewProcessById(id);
        return ResponseEntity.ok(interviewProcess);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<InterviewProcessDTO> updateInterviewProcess(@PathVariable UUID id, @RequestBody InterviewProcessDTO interviewProcessDTO) {
        logger.info("Updating interview process with ID: {}", id);
        InterviewProcessDTO updatedInterviewProcess = interviewProcessService.updateInterviewProcess(id, interviewProcessDTO);
        return ResponseEntity.ok(updatedInterviewProcess);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInterviewProcess(@PathVariable UUID id) {
        logger.info("Deleting interview process with ID: {}", id);
        interviewProcessService.deleteInterviewProcess(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{processId}/candidate")
    public ResponseEntity<String> addCandidateToProcess(@RequestParam UUID candidateId, @RequestParam UUID processId) {
        logger.info("adding candidate with ID: {}", candidateId);
        interviewProcessService.addCandidateToProcess(candidateId, processId);
        return ResponseEntity.ok("Candidate added to process successfully.");
    }


    @PostMapping("/{processId}/stages")
    public ResponseEntity<InterviewStageDTO> addStageToProcess(@Valid @RequestBody InterviewStageDTO interviewStageDTO) {
        try {
            InterviewStageDTO createdStage = interviewProcessService.addStageToProcess(interviewStageDTO);
            return new ResponseEntity<>(createdStage, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Error adding stage to process: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{processId}/stages")
    public ResponseEntity<List<StageDetailsDTO>> getStagesDetails(@PathVariable UUID processId) {
        try {
            List<StageDetailsDTO> stageDetails = interviewProcessService.getStagesDetails(processId);
            return ResponseEntity.ok(stageDetails);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input or process not found: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Collections.emptyList());
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }

    @PostMapping("/{processId}/candidates")
    public ResponseEntity<String> addBulkCandidatesToProcess(
            @PathVariable UUID processId,
            @RequestBody List<UUID> candidateIds) {
        try {
            interviewProcessService.addBulkCandidatesToProcess(processId, candidateIds);
            return ResponseEntity.ok("Candidates added successfully to process " + processId);
        } catch (UserNotFoundException e) {
            logger.error("Error adding candidates: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ValidationException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
