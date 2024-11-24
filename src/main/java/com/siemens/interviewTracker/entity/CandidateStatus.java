package com.siemens.interviewTracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "candidate_status")
public class CandidateStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "interview_process_id", nullable = false)
    private InterviewProcess interviewProcess;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CandidateProcessStatus status;

    @ManyToOne
    @JoinColumn(name = "current_stage_id")
    private InterviewStage currentStage;

}
