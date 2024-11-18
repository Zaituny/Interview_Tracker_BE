package com.siemens.interviewTracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "interviews")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate; // Reference to Candidate entity

    @ManyToOne
    @JoinColumn(name = "interviewer_id", nullable = false)
    private User interviewer; // Reference to User entity for interviewer

    @ManyToOne
    @JoinColumn(name = "stage_id", nullable = false)
    private InterviewStage stage;

    @Column(name = "interview_date", nullable = false)
    private LocalDateTime interviewDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InterviewStatus status; // COMPLETED, UPCOMING
}
