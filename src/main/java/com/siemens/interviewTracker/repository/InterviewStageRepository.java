package com.siemens.interviewTracker.repository;

import com.siemens.interviewTracker.entity.InterviewStage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InterviewStageRepository extends JpaRepository<InterviewStage, UUID> {
    @Query("SELECT COALESCE(MAX(s.stageOrder), 0) FROM InterviewStage s WHERE s.interviewProcess.id = :processId")
    Optional<Integer> findMaxStageOrderByInterviewProcessId(@Param("processId") UUID processId);

}
