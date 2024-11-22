package com.siemens.interviewTracker.controller;

import com.siemens.interviewTracker.dto.InterviewStageDTO;
import com.siemens.interviewTracker.service.InterviewStageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

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

//    @PostMapping("/{stageId}/move-candidate/{candidateId}")
//    public ResponseEntity<Void> moveCandidateToNextStage(@PathVariable UUID stageId, @PathVariable UUID candidateId) {
//        try {
//            interviewStageService.moveCandidateToNextStage(candidateId, stageId);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (IllegalArgumentException e) {
//            logger.error("Error moving candidate to next stage: {}", e.getMessage());
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        } catch (Exception e) {
//            logger.error("Unexpected error occurred: {}", e.getMessage());
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

}