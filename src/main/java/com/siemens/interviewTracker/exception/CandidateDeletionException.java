package com.siemens.interviewTracker.exception;

import java.util.Map;

public class CandidateDeletionException extends RuntimeException {
    private final Map<String, Object> errorDetails;

    public CandidateDeletionException(Map<String, Object> errorDetails) {
        super("Candidate cannot be deleted because he is enrolled in a process");
        this.errorDetails = errorDetails;
    }

    public Map<String, Object> getErrorDetails() {
        return errorDetails;
    }
}
