package com.haiqua.backend.controller;

import com.haiqua.backend.dto.*;
import com.haiqua.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserRegistrationDto registrationDto) {

        log.info("Register API called for email: {}", registrationDto.getEmail());

        UserDto registeredUser = authService.registerUser(registrationDto);

        log.info("User registered successfully with email: {}", registeredUser.getEmail());

        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerfiyOtpDto verfiyOtpDto) {

        log.info("OTP verification request for email: {}", verfiyOtpDto.getEmail());

        authService.verifyOtp(verfiyOtpDto.getEmail(), verfiyOtpDto.getOtp());

        log.info("OTP verified successfully for email: {}", verfiyOtpDto.getEmail());

        return ResponseEntity.ok("OTP Verified Successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {

        log.info("Login API called for email: {}", loginRequestDto.getEmail());

        LoginResponseDto response = authService.loginUser(loginRequestDto);

        log.info("Login successful for email: {}", loginRequestDto.getEmail());

        return ResponseEntity.ok(response);
    }
}