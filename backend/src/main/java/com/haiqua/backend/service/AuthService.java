package com.haiqua.backend.service;

import com.haiqua.backend.dto.LoginRequestDto;
import com.haiqua.backend.dto.LoginResponseDto;
import com.haiqua.backend.dto.UserDto;
import com.haiqua.backend.dto.UserRegistrationDto;

public interface AuthService {

    UserDto registerUser(UserRegistrationDto registrationDto);

    void verifyOtp(String email, String otp);

    LoginResponseDto loginUser(LoginRequestDto loginRequestDto);
}
