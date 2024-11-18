package com.siemens.interviewTracker.controller;

import com.siemens.interviewTracker.dto.InterviewStageDTO;
import com.siemens.interviewTracker.service.InterviewStageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v0/interview-stage")
public class InterviewStageController {

    private static final Logger logger = LoggerFactory.getLogger(InterviewStageController.class);

    private final InterviewStageService interviewStageService;

    public InterviewStageController(InterviewStageService interviewStageService) {
        this.interviewStageService = interviewStageService;
    }

    @PostMapping
    public ResponseEntity<InterviewStageDTO> addStageToProcess(@Valid @RequestBody InterviewStageDTO interviewStageDTO) {
        try {
            InterviewStageDTO createdStage = interviewStageService.addStageToProcess(interviewStageDTO);
            return new ResponseEntity<>(createdStage, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Error adding stage to process: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}