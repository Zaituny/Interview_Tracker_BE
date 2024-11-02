package com.siemens.interviewTracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.security.SecureRandom;
import org.springframework.stereotype.Service;
import com.siemens.interviewTracker.dto.UserDTO;
import com.siemens.interviewTracker.entity.User;
import com.siemens.interviewTracker.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.siemens.interviewTracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import static com.siemens.interviewTracker.utils.PasswordValidator.validateRawPassword;


@Service
public class AuthService {

    private final UserMapper userMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SendGridEmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final static Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    public AuthService(UserMapper userMapper,
                       UserService userService,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       SendGridEmailService emailService,
                       JwtTokenProvider jwtTokenProvider) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public UserDTO signup(UserDTO userDTO) {
        logger.info("Signing up new user with email: {}", userDTO.getEmail());
        UserDTO createdUser = userService.createUser(userDTO);
        String accessToken = jwtTokenProvider.generateAccessToken(userDTO.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDTO.getEmail());

        createdUser.setAccessToken(accessToken);
        createdUser.setRefreshToken(refreshToken);

        userRepository.save(userMapper.userDTOToUser(createdUser));
        logger.info("User signed up successfully with email: {}", userDTO.getEmail());

        return createdUser;
    }

    public UserDTO login(String email, String password) {
        logger.info("Attempting to log in user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Login failed: Incorrect password for email: {}", email);
            throw new IllegalArgumentException("Wrong password");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(email);
        String refreshToken = jwtTokenProvider.generateRefreshToken(email);

        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshToken);

        userRepository.save(user);
        logger.info("User logged in successfully with email: {}", email);

        return userMapper.userToUserDTO(user);
    }

    public void logout(String id) {
        logger.info("Logging out user with ID: {}", id);
        // TODO: implement method logic.
        logger.info("User with ID: {} has been logged out", id);
    }

    public String generateAccessToken(String refreshToken) {
        logger.info("Generating new access token from refresh token");
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            logger.error("Invalid refresh token provided");
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        String accessToken = jwtTokenProvider.generateAccessToken(email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setAccessToken(accessToken);

        userRepository.save(user);
        logger.info("New access token generated and saved for user with email: {}", email);

        return accessToken;
    }

    public String forgotPassword(String email){
        logger.info("Processing forgot password request for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = generateResetPasswordToken();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenDate(LocalDateTime.now());

        userRepository.save(user);

        String subject = "Password Reset Request";
        String body = "Your password reset token is: " + token;
        emailService.sendEmail(email, subject, body);

        logger.info("Password reset token sent to email: {}", email);
        return "Password reset token was sent to your email";
    }

    public UserDTO resetPassword(String email, String newPassword, String resetPasswordToken) {
        logger.info("Resetting password for user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!validateResetPasswordToken(email, resetPasswordToken)) {
            logger.warn("Invalid or expired reset password token for email: {}", email);
            throw new IllegalArgumentException("Invalid or expired reset password token");
        }

        validateRawPassword(newPassword);

        user.setResetPasswordToken(null);
        user.setResetPasswordTokenDate(null);
        user.setPassword(passwordEncoder.encode(newPassword));

        String accessToken = jwtTokenProvider.generateAccessToken(email);
        String refreshToken = jwtTokenProvider.generateRefreshToken(email);

        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshToken);

        userRepository.save(user);
        logger.info("Password reset successfully for user with email: {}", email);

        return userMapper.userToUserDTO(user);
    }

    public boolean validateResetPasswordToken(String email , String token) {
        logger.info("Validating reset password token for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean isValid = token.equals(user.getResetPasswordToken())
                && !isResetPasswordTokenExpired(user.getResetPasswordTokenDate());

        if (isValid) {
            logger.info("Reset password token is valid for email: {}", email);
        } else {
            logger.warn("Invalid or expired reset password token for email: {}", email);
        }
        return isValid;
    }

    private String generateResetPasswordToken() {
        SecureRandom secureRandom = new SecureRandom();
        int code = secureRandom.nextInt(900000) + 100000; // Generates a random 6-digit number
        logger.info("Generated reset password token");
        return String.valueOf(code);
    }

    private boolean isResetPasswordTokenExpired(final LocalDateTime tokenCreationDate) {
        LocalDateTime expiryTime = tokenCreationDate.plusMinutes(10);
        boolean isExpired = LocalDateTime.now().isAfter(expiryTime);
        if (isExpired) {
            logger.info("Reset password token is expired");
        }
        return isExpired;
    }
}
