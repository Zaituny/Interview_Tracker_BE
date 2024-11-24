package com.siemens.interviewTracker.repository;

import com.siemens.interviewTracker.dto.StageDetailsDTO;
import com.siemens.interviewTracker.entity.InterviewStage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InterviewStageRepository extends JpaRepository<InterviewStage, UUID> {
    @Query("SELECT COALESCE(MAX(s.stageOrder), 0) FROM InterviewStage s WHERE s.interviewProcess.id = :processId")
    Optional<Integer> findMaxStageOrderByInterviewProcessId(@Param("processId") UUID processId);

    @Query("SELECT s FROM InterviewStage s WHERE s.interviewProcess.id = :processId ORDER BY s.stageOrder ASC")
    List<InterviewStage> findStagesByInterviewProcessId(@Param("processId") UUID processId, Pageable pageable);

    // Method to find the next stage based on the current stage's order
    @Query("SELECT s FROM InterviewStage s WHERE s.interviewProcess.id = :processId AND s.stageOrder = :nextOrder")
    Optional<InterviewStage> findNextStage(@Param("processId") UUID processId, @Param("nextOrder") int nextOrder);

    @Query("SELECT s FROM InterviewStage s WHERE s.interviewProcess.id = :processId AND s.stageOrder > :stageOrder ORDER BY s.stageOrder")
    List<InterviewStage> findByInterviewProcessIdAndStageOrderGreaterThan(
            @Param("processId") UUID processId,
            @Param("stageOrder") Integer stageOrder);

    @Query("SELECT s FROM InterviewStage s WHERE s.interviewProcess.id = :processId AND s.stageOrder = 1")
    Optional<InterviewStage> findFirstStageByProcessId(@Param("processId") UUID processId);


    @Query("""
    SELECT new com.siemens.interviewTracker.dto.StageDetailsDTO(
        s.name,
        s.id,
        s.status,
        s.stageOrder,
        SIZE(s.candidates),
        SIZE(s.interviewers)
    )
    FROM InterviewStage s
    WHERE s.interviewProcess.id = :processId
    ORDER BY s.stageOrder ASC
""")
    List<StageDetailsDTO> findStageDetailsByProcessId(@Param("processId")UUID processId);

    Optional<InterviewStage> findByInterviewProcessIdAndStageOrder(UUID processId, int stageOrder);
}






