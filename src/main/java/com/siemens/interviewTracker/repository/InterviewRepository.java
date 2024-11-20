package com.siemens.interviewTracker.repository;

import com.siemens.interviewTracker.dto.StageDetailsDTO;
import com.siemens.interviewTracker.entity.InterviewStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InterviewRepository extends JpaRepository<InterviewStage, UUID> {

    @Query("""
    SELECT new com.siemens.interviewTracker.dto.StageDetailsDTO(
        s.name,
        s.stageOrder,
        COUNT(DISTINCT i.candidate.id),
        COUNT(DISTINCT i.interviewer.id),
        SUM(CASE WHEN i.status = 'COMPLETED' THEN 1 ELSE 0 END),
        SUM(CASE WHEN i.status = 'UPCOMING' THEN 1 ELSE 0 END)
    )
    FROM InterviewStage s
    LEFT JOIN Interview i ON s.id = i.stage.id
    WHERE s.interviewProcess.id = :processId
    GROUP BY s.id, s.stageOrder
    Order By s.stageOrder ASC
""")
    List<StageDetailsDTO> findStageDetailsByProcessId(@Param("processId")UUID processId);
}
