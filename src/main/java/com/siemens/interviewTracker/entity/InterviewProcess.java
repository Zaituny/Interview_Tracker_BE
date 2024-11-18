package com.siemens.interviewTracker.entity;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;


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
    @Column(name = "title")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InterviewProcessStatus status;

    @Email
    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToMany(mappedBy = "interviewProcesses")
    private Set<Candidate> candidates = new HashSet<>();

    @OneToMany(mappedBy = "interviewProcess")
    private Set<InterviewStage> interviewStages = new HashSet<>();
}
