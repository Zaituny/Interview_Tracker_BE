package com.siemens.interviewTracker.controller;

import com.siemens.interviewTracker.dto.FeedbackDTO;
import com.siemens.interviewTracker.service.FeedbackService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v0/feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;
    private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<FeedbackDTO> createFeedback(@RequestBody FeedbackDTO feedbackDTO) {
        logger.info("Creating feedback with id: {}", feedbackDTO.getId());
        FeedbackDTO createdFeedback = feedbackService.createFeedback(feedbackDTO);
        logger.info("Feedback created with ID: {}", createdFeedback.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFeedback);
    }

    @GetMapping("/by/{candidateId}/in/{stageId}")
    public ResponseEntity<List<FeedbackDTO>> getAllFeedbacksByCandidate(@PathVariable UUID candidateId, @PathVariable UUID stageId,@RequestParam(defaultValue = "10") int limit,
                                                                        @RequestParam(defaultValue = "0") int offset) {
        logger.info("Fetching all feedbacks for candidate with id: {}", candidateId);
        List<FeedbackDTO> feedbacks = feedbackService.getAllFeedbacksByCandidateInStage(candidateId, stageId,limit, offset).getContent();
        return ResponseEntity.ok(feedbacks);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<FeedbackDTO> updateFeedback(@PathVariable UUID id, @RequestBody FeedbackDTO feedbackDTO) {
        logger.info("Updating feedback with ID: {}", id);
        FeedbackDTO updatedFeedback = feedbackService.updateFeedback(id, feedbackDTO);
        return ResponseEntity.ok(updatedFeedback);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable UUID id) {
        logger.info("Deleting feedback with ID: {}", id);
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
}
