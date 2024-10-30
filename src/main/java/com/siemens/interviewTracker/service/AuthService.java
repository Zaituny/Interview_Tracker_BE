package com.siemens.interviewTracker.service;

import com.siemens.interviewTracker.dto.UserDTO;
import com.siemens.interviewTracker.entity.User;
import com.siemens.interviewTracker.mapper.UserMapper;
import com.siemens.interviewTracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

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
