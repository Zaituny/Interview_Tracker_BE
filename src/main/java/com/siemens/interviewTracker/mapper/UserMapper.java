package com.siemens.interviewTracker.mapper;

import com.siemens.interviewTracker.entity.User;
import com.siemens.interviewTracker.dto.UserDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO userToUserDTO(User user);

    User userDTOToUser(UserDTO userDTO);
}
