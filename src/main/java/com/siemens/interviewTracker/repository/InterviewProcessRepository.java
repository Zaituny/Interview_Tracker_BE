package com.siemens.interviewTracker.repository;

import java.util.UUID;
import org.springframework.stereotype.Repository;
import com.siemens.interviewTracker.entity.InterviewProcess;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface InterviewProcessRepository extends JpaRepository<InterviewProcess, UUID> {
}
