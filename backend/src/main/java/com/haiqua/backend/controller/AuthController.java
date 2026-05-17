package com.haiqua.backend.controller;

import com.haiqua.backend.dto.*;
import com.haiqua.backend.service.AuthService;
import com.haiqua.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {


    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserRegistrationDto registrationDto) {
        UserDto registeredUser = authService.registerUser(registrationDto);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerfiyOtpDto verfiyOtpDto){
        authService.verifyOtp(verfiyOtpDto.getEmail(),verfiyOtpDto.getOtp());

        return ResponseEntity.ok("OTP Verified Successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){

        LoginResponseDto response = authService.loginUser(loginRequestDto);
        return ResponseEntity.ok(response);
    }

}
