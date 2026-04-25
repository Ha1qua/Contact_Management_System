package com.haiqua.backend.service;

import com.haiqua.backend.dto.UserDto;
import com.haiqua.backend.dto.UserRegistrationDto;

public interface UserService {

    UserDto registerUser(UserRegistrationDto registrationDto);
}
