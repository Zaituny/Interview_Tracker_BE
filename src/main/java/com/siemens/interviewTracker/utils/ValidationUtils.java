package com.siemens.interviewTracker.utils;

import java.util.Set;
import jakarta.validation.ConstraintViolation;
import com.siemens.interviewTracker.dto.UserDTO;


public class ValidationUtils {

    public static String getValidationErrors(Set<ConstraintViolation<UserDTO>> constraintViolations) {
        StringBuilder errors = new StringBuilder();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            errors.append(constraintViolation.getPropertyPath()).append(": ")
                    .append(constraintViolation.getMessage()).append("\n");
        }
        return errors.toString();
    }
}
