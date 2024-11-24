package com.siemens.interviewTracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "interview_process")
public class InterviewProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotBlank(message = "Title cannot be empty")
    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InterviewProcessStatus status;

    @Email
    @NotBlank
    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
            name = "interview_process_candidates",
            joinColumns = @JoinColumn(name = "interview_process_id"),
            inverseJoinColumns = @JoinColumn(name = "candidate_id")
    )
    private Set<Candidate> candidates = new HashSet<>();

    @OneToMany(mappedBy = "interviewProcess" , cascade = CascadeType.ALL)
    private Set<InterviewStage> interviewStages = new HashSet<>();

    @OneToMany(mappedBy = "interviewProcess", cascade = CascadeType.ALL)
    private Set<CandidateStatus> candidateStatuses = new HashSet<>();
}
