package com.siemens.interviewTracker.repository;

import com.siemens.interviewTracker.entity.InterviewStage;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@Repository
public interface InterviewStageRepository extends JpaRepository<InterviewStage, UUID> {

}
