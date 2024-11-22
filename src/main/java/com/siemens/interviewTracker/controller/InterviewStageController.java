package com.siemens.interviewTracker.controller;

import com.siemens.interviewTracker.dto.InterviewStageDTO;
import com.siemens.interviewTracker.entity.Candidate;
import com.siemens.interviewTracker.entity.User;
import com.siemens.interviewTracker.service.InterviewStageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("api/v0/interview-stage")
public class InterviewStageController {

    private static final Logger logger = LoggerFactory.getLogger(InterviewStageController.class);

    private final InterviewStageService interviewStageService;

    public InterviewStageController(InterviewStageService interviewStageService) {
        this.interviewStageService = interviewStageService;
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

    @PostMapping("/{stageId}/add-interviewer/{interviewerId}")
    public ResponseEntity<Void> addInterviewerToStage(@PathVariable UUID stageId, @PathVariable UUID interviewerId) {
        try {
            interviewStageService.addInterviewerToStage(stageId, interviewerId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Error adding interviewer to stage: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{stageId}/interviewers")
    public ResponseEntity<Set<UUID>> getInterviewersForStage(@PathVariable UUID stageId) {
        try {
            Set<UUID> interviewersIds = interviewStageService.getInterviewersForStage(stageId);
            return new ResponseEntity<>(interviewersIds, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Error fetching interviewers for stage: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{stageId}/candidates")
    public ResponseEntity<Set<UUID>> getCandidatesForStage(@PathVariable UUID stageId) {
        try {
            Set<UUID> candidatesIds = interviewStageService.getCandidatesForStage(stageId);
            return new ResponseEntity<>(candidatesIds, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Error fetching candidates for stage: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{stageId}/delete-candidate/{candidateId}")
    public ResponseEntity<Void> deleteCandidateFromStage(@PathVariable UUID stageId, @PathVariable UUID candidateId) {
        try {
            interviewStageService.deleteCandidateFromStage(stageId, candidateId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting candidate from stage: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{stageId}/delete-interviewer/{interviewerId}")
    public ResponseEntity<Void> deleteInterviewerFromStage(@PathVariable UUID stageId, @PathVariable UUID interviewerId) {
        try {
            interviewStageService.deleteInterviewerFromStage(stageId, interviewerId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting interviewer from stage: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}