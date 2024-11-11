package com.siemens.interviewTracker.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "job_position")
public class JobPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotBlank(message = "Title cannot be empty")
    @Column(name = "title")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;

    @ElementCollection
    @CollectionTable(
            name = "job_position_requirements",
            joinColumns = @JoinColumn(name = "job_position_id")
    )
    @Column(name = "requirements")
    private List<String> requirements;

    @ElementCollection
    @CollectionTable(
            name = "job_position_responsibilities",
            joinColumns = @JoinColumn(name = "job_position_id")
    )
    @Column(name = "responsibilities")
    private List<String> responsibilities;

}
