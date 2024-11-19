package com.siemens.interviewTracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name="feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name="stage_id", nullable = false)
    private InterviewStage stage_id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user_id;

    @ManyToOne
    @JoinColumn(name="candidate_id", nullable = false)
    private Candidate candidate_id;

    @Column(name="rating", nullable = false)
    private String rating;

    @Column(name="comments")
    private String comments;
}
