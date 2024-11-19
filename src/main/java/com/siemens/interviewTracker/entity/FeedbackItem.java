package com.siemens.interviewTracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name="feedback_item")
public class FeedbackItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name="feedback_form_id", nullable = false)
    private Feedback feedbackForm;

    @Column(name="question", nullable = false)
    private String question;

    @Column(name="answer", nullable = false)
    private String answer;
}
