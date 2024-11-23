package com.siemens.interviewTracker.controller;

import com.siemens.interviewTracker.dto.CandidateDTO;
import com.siemens.interviewTracker.service.CandidateService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/v0/candidates")
public class CandidateController {
    private final CandidateService candidateService;
    private static final Logger logger = LoggerFactory.getLogger(CandidateController.class);
    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @PostMapping
    public ResponseEntity<CandidateDTO> createCandidate(@Valid @RequestBody CandidateDTO candidateDTO) {
        logger.info("Creating candidate with email: {}", candidateDTO.getEmail());
        CandidateDTO createdCandidate = candidateService.createCandidate(candidateDTO);
        logger.info("Candidate created with ID: {}", createdCandidate.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCandidate);
    }

    @GetMapping
    public ResponseEntity<List<CandidateDTO>> getAllCandidates(@RequestParam(defaultValue = "10") int limit,
                                                               @RequestParam(defaultValue = "0") int offset) {
        logger.info("Fetching candidates with limit: {}, offset: {}", limit, offset);
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100.");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be non-negative.");
        }

        List<CandidateDTO> candidates = candidateService.getAllCandidates(limit, offset).getContent();
        return ResponseEntity.ok(candidates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidateDTO> getCandidateById(@PathVariable UUID id) {
        logger.info("Fetching candidate with ID: {}", id);
        CandidateDTO candidate = candidateService.getCandidateById(id);
        return ResponseEntity.ok(candidate);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CandidateDTO> updateCandidate(@PathVariable UUID id, @RequestBody CandidateDTO candidateDTO) {
        logger.info("Updating candidate with ID: {}", id);
        CandidateDTO updatedCandidate = candidateService.updateCandidate(id, candidateDTO);
        return ResponseEntity.ok(updatedCandidate);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable UUID id) {
        logger.info("Deleting candidate with ID: {}", id);
        candidateService.deleteCandidate(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/candidate/{processId}")
    public CandidateDTO createCandidateAndAddToProcess(@RequestBody CandidateDTO candidateDTO, @PathVariable UUID processId) {
        return candidateService.createCandidateAndAddToProcess(candidateDTO, processId);
    }

    @GetMapping("/interview-process/{interviewProcessId}")
    public ResponseEntity<Page<CandidateDTO>> getCandidatesByInterviewProcess(
            @PathVariable UUID interviewProcessId,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        logger.info("Fetching candidates for interview process ID: {}, with limit: {}, offset: {}", interviewProcessId, limit, offset);
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100.");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be non-negative.");
        }

        Page<CandidateDTO> candidates = candidateService.getCandidatesInInterviewProcess(interviewProcessId, limit, offset);
        return ResponseEntity.ok(candidates);
    }

    @GetMapping("/interview-stage/{interviewStageId}")
    public ResponseEntity<Page<CandidateDTO>> getCandidatesByInterviewStage(
            @PathVariable UUID interviewStageId,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        logger.info("Fetching candidates for interview stage ID: {}, with limit: {}, offset: {}", interviewStageId, limit, offset);
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100.");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be non-negative.");
        }

        Page<CandidateDTO> candidates = candidateService.getCandidatesInInterviewStage(interviewStageId, limit, offset);
        return ResponseEntity.ok(candidates);
    }
}
