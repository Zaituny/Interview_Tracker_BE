package com.siemens.interviewTracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "interview_stage")
public class InterviewStage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne // Many InterviewStages can be linked to one InterviewProcess
    @JoinColumn(name = "interview_process_id", nullable = false) // Foreign key column name
    private InterviewProcess interviewProcess;

    @NotBlank(message = "Stage name cannot be empty")
    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING) // Save as a string in the database
    @Column(name = "status")
    private InterviewStageStatus status;

    @ManyToMany(mappedBy = "interviewStages")
    private Set<Candidate> candidates = new HashSet<>();

}
