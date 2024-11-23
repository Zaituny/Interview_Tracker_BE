package com.siemens.interviewTracker.controller;

import com.siemens.interviewTracker.dto.InterviewProcessDTO;
import com.siemens.interviewTracker.dto.InterviewStageDTO;
import com.siemens.interviewTracker.service.InterviewStageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v0/interview-stage")
public class InterviewStageController {

    private static final Logger logger = LoggerFactory.getLogger(InterviewStageController.class);

    private final InterviewStageService interviewStageService;

    public InterviewStageController(InterviewStageService interviewStageService) {
        this.interviewStageService = interviewStageService;
    }

    @GetMapping
    public ResponseEntity<List<InterviewStageDTO>> getAllInterviewStages(@RequestParam(defaultValue = "10") int limit,
                                                                              @RequestParam(defaultValue = "0") int offset) {
        logger.info("Fetching interview stages with limit: {}, offset: {}", limit, offset);
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100.");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be non-negative.");
        }

        List<InterviewStageDTO> interviewStages = interviewStageService.getAllInterviewStages(limit, offset).getContent();
        return ResponseEntity.ok(interviewStages);
    }

    @PostMapping("/{stageId}/add-candidate/{candidateId}")
    public ResponseEntity<Void> addCandidateToStage(@PathVariable UUID stageId, @PathVariable UUID candidateId) {
        try {
            interviewStageService.addCandidateToStage(stageId, candidateId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Error adding candidate to stage: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{interviewStageId}/candidates-count")
    public ResponseEntity<Long> getCandidateCount(@PathVariable UUID interviewStageId) {
        try {
            long count = interviewStageService.getCandidateCountInStage(interviewStageId);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            logger.error("Error fetching candidate count: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{interviewStageId}/interviewers-count")
    public ResponseEntity<Long> getInterviewerCount(@PathVariable UUID interviewStageId) {
        try {
            long count = interviewStageService.getInterviewerCountInStage(interviewStageId);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            logger.error("Error fetching interviewer count: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

<<<<<<< HEAD
    @DeleteMapping("/{stageId}")
    public ResponseEntity<Void> deleteStage(@PathVariable UUID stageId) {
        try {
            interviewStageService.deleteInterviewStage(stageId);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            logger.error("Deletion restriction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // Return 409 Conflict
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting interview stage: {}", e.getMessage());
            return ResponseEntity.badRequest().build(); // Return 400 Bad Request
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage());
            return ResponseEntity.status(500).build(); // Return 500 Internal Server Error
        }
    }

    
=======
    @PostMapping("/{interviewStageId}/add-interviewers")
    public ResponseEntity<Void> addInterviewersToInterviewStage(@PathVariable UUID interviewStageId,
                                                                @RequestBody List<UUID> interviewerIds) {
        try {
            interviewStageService.addInterviewersToInterviewStage(interviewStageId, interviewerIds);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Error adding interviewer to stage: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
>>>>>>> main
}