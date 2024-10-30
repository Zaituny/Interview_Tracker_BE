package com.siemens.interviewTracker.mapper;

import com.siemens.interviewTracker.entity.User;
import com.siemens.interviewTracker.dto.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // Mapping from User entity to UserDTO

    UserDTO userToUserDTO(User user);

    // Mapping from UserDTO to User entity
    User userDTOToUser(UserDTO userDTO);
}

