package com.haiqua.backend.service;

import com.haiqua.backend.dto.*;

public interface AuthService {

    UserDto registerUser(UserRegistrationDto registrationDto);

    void verifyOtp(String email, String otp);

    LoginResponseDto loginUser(LoginRequestDto loginRequestDto);

    void resendOtp(String email);

    void resetPassword(String email, String newPassword);

    void changePassword(ChangePasswordRequestDto dto);

    UserProfileResponse getMyProfile();
}
