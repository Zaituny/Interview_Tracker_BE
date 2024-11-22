package com.siemens.interviewTracker.service;

import com.siemens.interviewTracker.dto.FeedbackDTO;
import com.siemens.interviewTracker.entity.Candidate;
import com.siemens.interviewTracker.entity.Feedback;
import com.siemens.interviewTracker.entity.InterviewStage;
import com.siemens.interviewTracker.entity.User;
import com.siemens.interviewTracker.mapper.FeedbackMapper;
import com.siemens.interviewTracker.repository.CandidateRepository;
import com.siemens.interviewTracker.repository.FeedbackRepository;
import com.siemens.interviewTracker.repository.InterviewStageRepository;
import com.siemens.interviewTracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

    private final InterviewStageRepository interviewStageRepository;
    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final FeedbackMapper feedbackMapper;
    private static final Logger logger = LoggerFactory.getLogger(FeedbackService.class);

    public FeedbackService(FeedbackRepository feedbackRepository,
                           InterviewStageRepository interviewStageRepository,
                           UserRepository userRepository,
                           CandidateRepository candidateRepository,
                           FeedbackMapper feedbackMapper) {
        this.feedbackRepository = feedbackRepository;
        this.interviewStageRepository = interviewStageRepository;
        this.userRepository = userRepository;
        this.candidateRepository = candidateRepository;
        this.feedbackMapper = feedbackMapper;
    }

    public FeedbackDTO createFeedback(FeedbackDTO feedbackDTO){
        logger.debug("Validating feedback for creation: {}", feedbackDTO);

        if (feedbackDTO == null) {
            throw new IllegalArgumentException("Feedback cannot be null");
        }
        return feedbackMapper.toDTO(feedbackRepository.save(feedbackMapper.toEntity(feedbackDTO)));
    }

    public FeedbackDTO getFeedbackById(UUID feedbackId){
        logger.debug("Fetching feedback by id: {}", feedbackId);
        return feedbackMapper.toDTO(feedbackRepository.findById(feedbackId).orElse(null));
    }

    public Page<FeedbackDTO> getAllFeedbacksByCandidateInStage(UUID candidateId, UUID stageId,int limit, int offset){
        logger.debug("Fetching all feedbacks for candidate with id: {}", candidateId);
        if (limit < 1 || offset < 0) {
            throw new IllegalArgumentException("Limit must be greater than 0 and offset must be non-negative.");
        }
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Feedback> feedbacks = feedbackRepository.findByCandidateIdAndStageId(candidateId, stageId,pageable);
        return feedbacks.map(feedbackMapper::toDTO);
    }

    public FeedbackDTO updateFeedback(UUID feedbackId, FeedbackDTO feedbackDTO){
        logger.debug("Validating feedback for update: {}", feedbackDTO);

        if (feedbackDTO == null) {
            throw new IllegalArgumentException("Feedback cannot be null");
        }

        return feedbackRepository.findById(feedbackId)
                .map(existingFeedback -> {
                    if(feedbackDTO.getCandidateId() != null){
                        Candidate candidate= candidateRepository.findById(feedbackDTO.getCandidateId()).orElse(null);
                        if (candidate == null) {
                            throw new IllegalArgumentException("Candidate with id " + feedbackDTO.getCandidateId() + " not found");
                        }
                        existingFeedback.setCandidate(candidate);
                    }
                    if(feedbackDTO.getStageId() != null){
                        InterviewStage stage = interviewStageRepository.findById(feedbackDTO.getStageId()).orElse(null);
                        if(stage == null){
                            throw new IllegalArgumentException("Stage with id " + feedbackDTO.getStageId() + " not found");
                        }
                        existingFeedback.setStage(stage);
                    }
                    if(feedbackDTO.getUserId() != null){
                        User user = userRepository.findById(feedbackDTO.getUserId()).orElse(null);
                        if(user == null){
                            throw new IllegalArgumentException("User with id " + feedbackDTO.getUserId() + " not found");
                        }
                        existingFeedback.setUser(user);
                    }
                    if(feedbackDTO.getComments() != null){
                        existingFeedback.setComments(feedbackDTO.getComments());
                    }
                    return feedbackRepository.save(existingFeedback);
                })
                .map(feedbackMapper::toDTO)
                .orElseThrow(() ->{
                    logger.error("Feedback with id {} not found", feedbackId);
                    return new IllegalArgumentException("Feedback with id " + feedbackId + " not found");
                });
    }

    public void deleteFeedback(UUID feedbackId){
        logger.debug("Deleting feedback with id: {}", feedbackId);
        feedbackRepository.deleteById(feedbackId);
    }


}
