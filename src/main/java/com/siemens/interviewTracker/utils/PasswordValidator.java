package com.siemens.interviewTracker.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PasswordValidator {

    private static final Logger logger = LoggerFactory.getLogger(PasswordValidator.class);
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,100}$";

    public static void validateRawPassword(String rawPassword) {
        if (rawPassword.length() < 8 || rawPassword.length() > 49) {
            logger.error("Password length invalid");
            throw new IllegalArgumentException("Password must be between 8 and 49 characters long");
        }

        if (!rawPassword.matches(PASSWORD_REGEX)) {
            logger.error("Password does not meet complexity requirements");
            throw new IllegalArgumentException("Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character.");
        }
    }
}
