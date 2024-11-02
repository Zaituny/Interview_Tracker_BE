package com.siemens.interviewTracker.mapper;

import org.mapstruct.Mapper;
import com.siemens.interviewTracker.entity.User;
import com.siemens.interviewTracker.dto.UserDTO;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO userToUserDTO(User user);

    User userDTOToUser(UserDTO userDTO);
}
