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
    private UserService userService;  // Inject UserService for additional user-related operations

    public String forgotPass(String email){
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(!userOptional.isPresent()){
            return "Invalid email id.";
        }

        UserDTO userDTO = userMapper.userToUserDTO(userOptional.get());
        userDTO.setPasswordToken(generateToken());
        userDTO.setPasswordTokenDate(LocalDateTime.now());

        User updatedUser = userMapper.userDTOToUser(userDTO);
        userService.updateUser(updatedUser.getId() , updatedUser);

        // logic to send email

        return "Password reset token was sent to your email";
    }

    public String resetPass(String token, String password) {
        Optional<User> userOptional = userRepository.findByPasswordToken(token);

        if (!userOptional.isPresent()) {
            return "Invalid token";
        }

        UserDTO userDTO = userMapper.userToUserDTO(userOptional.get());

        // Check if the token has expired
        if (isTokenExpired(userDTO.getPasswordTokenDate())) {
            return "Token expired.";
        }

        userDTO.setPassword(password);
        userDTO.setPasswordToken(null);
        userDTO.setPasswordTokenDate(null);

        // Convert UserDTO back to User entity and save to the database
        User updatedUser = userMapper.userDTOToUser(userDTO);
        userService.updateUser(updatedUser.getId() , updatedUser);

        return "Your password was successfully updated.";
    }


    private String generateToken() {
        StringBuilder token = new StringBuilder();

        return token.append(UUID.randomUUID().toString())
                .append(UUID.randomUUID().toString()).toString();
    }

    private boolean isTokenExpired(final LocalDateTime tokenCreationDate) {

        LocalDateTime expiryTime = tokenCreationDate.plusMinutes(10);
        return LocalDateTime.now().isBefore(expiryTime);
    }

}
