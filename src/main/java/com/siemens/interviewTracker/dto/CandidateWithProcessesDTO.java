package com.siemens.interviewTracker.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CandidateWithProcessesDTO {
    private UUID id;
    private String email;
    private String name;
    private String phone;
    private String address;
    private String resumePath;
    private List<ProcessDetailDTO> processes;
}

