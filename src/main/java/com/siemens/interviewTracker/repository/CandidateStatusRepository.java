package com.siemens.interviewTracker.repository;

import com.siemens.interviewTracker.entity.Candidate;
import com.siemens.interviewTracker.entity.CandidateStatus;
import com.siemens.interviewTracker.entity.InterviewProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateStatusRepository extends JpaRepository<CandidateStatus, Long> {
    Optional<CandidateStatus> findByCandidateAndInterviewProcess(Candidate candidate, InterviewProcess process);
}
