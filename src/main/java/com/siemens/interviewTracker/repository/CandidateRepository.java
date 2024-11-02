package com.siemens.interviewTracker.repository;

import java.util.UUID;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.siemens.interviewTracker.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface CandidateRepository extends JpaRepository<Candidate, UUID> {

    Optional<Candidate> findByEmail(String email);

    Optional<Candidate> findByPhone(String phone);
}
