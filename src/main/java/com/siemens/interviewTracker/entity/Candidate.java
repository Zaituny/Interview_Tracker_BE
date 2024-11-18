package com.siemens.interviewTracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "candidates")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Email
    @NotBlank(message = "Email cannot be empty")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Size(min = 4, max = 50)
    @NotBlank(message = "Name cannot be empty")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "resume_path")
    private String resumePath;

    @ManyToMany
    @JoinTable(
            name = "candidate_tags",
            joinColumns = @JoinColumn(name = "candidate_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany(mappedBy = "candidates")
    private Set<InterviewProcess> interviewProcesses = new HashSet<>();

    // Helper methods
    public void addInterviewProcess(InterviewProcess interviewProcess) {
        this.interviewProcesses.add(interviewProcess);
        interviewProcess.getCandidates().add(this);
    }

    public void removeInterviewProcess(InterviewProcess interviewProcess) {
        this.interviewProcesses.remove(interviewProcess);
        interviewProcess.getCandidates().remove(this);
    }
}
