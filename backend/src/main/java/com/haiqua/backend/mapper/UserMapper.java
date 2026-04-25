package com.haiqua.backend.mapper;

import com.haiqua.backend.dto.UserDto;
import com.haiqua.backend.dto.UserRegistrationDto;
import com.haiqua.backend.entity.User;

public class UserMapper {

    public static UserDto mapToUserDto(User user){
       return new UserDto(
               user.getId(),
               user.getEmail(),
               user.isVerified()
       );
    }

    public static User mapToUser(UserRegistrationDto regDto){
        User user = new User();
        user.setEmail(regDto.getEmail());
        user.setPassword(regDto.getPassword());
        user.setVerified(false);
        return user;
    }


}
