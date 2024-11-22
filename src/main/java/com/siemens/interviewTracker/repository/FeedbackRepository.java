package com.siemens.interviewTracker.repository;

import com.siemens.interviewTracker.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    Page<Feedback> findByCandidate_IdAndStage_Id(UUID candidateId, UUID stageId, Pageable pageable);

}
