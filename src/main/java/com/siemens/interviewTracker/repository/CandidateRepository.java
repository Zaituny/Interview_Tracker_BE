package com.siemens.interviewTracker.repository;

import java.util.UUID;
import java.util.Optional;

import com.siemens.interviewTracker.entity.InterviewProcess;
import com.siemens.interviewTracker.entity.InterviewStage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import com.siemens.interviewTracker.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface CandidateRepository extends JpaRepository<Candidate, UUID> {

    Optional<Candidate> findByEmail(String email);

    Optional<Candidate> findByPhone(String phone);

    Page<Candidate> findByInterviewProcessesContaining(InterviewProcess interviewProcess, Pageable pageable);

    Page<Candidate> findByInterviewStagesContaining(InterviewStage interviewStage, Pageable pageable);
}
