package com.siemens.interviewTracker.entity;

import lombok.Getter;
import lombok.Setter;
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

    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_position_id", nullable = false)
    private JobPosition jobPosition;
}
