package com.siemens.interviewTracker.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.siemens.interviewTracker.entity.InterviewProcess;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface InterviewProcessRepository extends JpaRepository<InterviewProcess, UUID> {

    @Query("SELECT COUNT(ip) FROM InterviewProcess ip WHERE EXTRACT(MONTH FROM ip.createdAt) = :month AND EXTRACT(YEAR FROM ip.createdAt) = :year")
    int countInterviewProcessesByMonthAndYear(@Param("month") int month, @Param("year") int year);
}
