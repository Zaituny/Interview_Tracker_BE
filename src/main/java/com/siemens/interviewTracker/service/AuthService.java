package com.siemens.interviewTracker.service;

import com.siemens.interviewTracker.dto.UserDTO;
import com.siemens.interviewTracker.entity.User;
import com.siemens.interviewTracker.mapper.UserMapper;
import com.siemens.interviewTracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import java.util.Set;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final UserMapper userMapper;
    private final UserService userService; // Changed to final
    private final EmailService emailService; // Changed to final
    private final static String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,100}$";

    // Constructor Injection
    @Autowired
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       Validator validator,
                       UserMapper userMapper,
                       UserService userService,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
        this.userMapper = userMapper;
        this.userService = userService;
        this.emailService = emailService;
    }
    public User signup(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException("Validation errors: " + violations);
        }
        validateRawPassword(userDTO.getPassword());
        User user = userMapper.userDTOToUser(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return userRepository.save(user);
    }
    private void validateRawPassword(String rawPassword) {
        if (rawPassword.length() < 8 || rawPassword.length() > 49) {
            throw new IllegalArgumentException("Password must be between 8 and 49 characters");
        }
        if (!rawPassword.matches(PASSWORD_REGEX)) {
            throw new IllegalArgumentException("Password must contain at least one uppercase, lowercase, number, and special character");
        }
    }
    public String forgotPass(String email){
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(!userOptional.isPresent()){
            return "Invalid email id.";
        }

        UserDTO userDTO = userMapper.userToUserDTO(userOptional.get());
        String token = generateToken();
        userDTO.setPasswordToken(token);
        userDTO.setPasswordTokenDate(LocalDateTime.now());

        User updatedUser = userMapper.userDTOToUser(userDTO);
        userService.updateUser(updatedUser.getId() , updatedUser);

        // logic to send email
        String subject = "Password Reset Request";
        String body = "Your password reset token is: " + token;
        emailService.sendSimpleEmail(email, subject, body);

        return "Password reset token was sent to your email";
    }

    public String resetPass(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (!userOptional.isPresent()) {
            return "Invalid email";
        }

        UserDTO userDTO = userMapper.userToUserDTO(userOptional.get());

        userDTO.setPassword(password);
        userDTO.setPasswordToken(null);
        userDTO.setPasswordTokenDate(null);

        // Convert UserDTO back to User entity and save to the database
        User updatedUser = userMapper.userDTOToUser(userDTO);
        userService.updateUser(updatedUser.getId() , updatedUser);

        return "Your password was successfully updated.";
    }

    public boolean validateToken(String email , String token) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (!userOptional.isPresent()) {
            return false;
        }

        UserDTO userDTO = userMapper.userToUserDTO(userOptional.get());
        if(token.equals(userDTO.getPasswordToken()) && !isTokenExpired(userDTO.getPasswordTokenDate())){
            return true;
        }
        return false;
    }
    private String generateToken() {
        int code = (int) (Math.random() * 90000) + 10000; // Generates a random 5-digit number
        return String.valueOf(code);
    }

    private boolean isTokenExpired(final LocalDateTime tokenCreationDate) {

        LocalDateTime expiryTime = tokenCreationDate.plusMinutes(10);
        return LocalDateTime.now().isBefore(expiryTime);
    }
}
