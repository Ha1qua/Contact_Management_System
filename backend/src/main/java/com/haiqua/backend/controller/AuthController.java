package com.haiqua.backend.controller;

import com.haiqua.backend.dto.*;
import com.haiqua.backend.service.AuthService;
import jakarta.validation.Valid;
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
    public ResponseEntity<ApiResponse<UserDto>> register(
            @Valid @RequestBody UserRegistrationDto registrationDto
    ) {

        UserDto registeredUser = authService.registerUser(registrationDto);

        ApiResponse<UserDto> response =
                new ApiResponse<>(
                        true,
                        "User registered successfully",
                        registeredUser
                );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@RequestBody VerifyOtpDto verifyOtpDto) {

        log.info("OTP verification request for email: {}", verifyOtpDto.getEmail());

        authService.verifyOtp(verifyOtpDto.getEmail(), verifyOtpDto.getOtp());

        log.info("OTP verified successfully for email: {}", verifyOtpDto.getEmail());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "OTP verified successfully", null)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @RequestBody LoginRequestDto loginRequestDto
    ) {

        LoginResponseDto response = authService.loginUser(loginRequestDto);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Login successful",
                        response
                )
        );
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody ResendOtpRequest request) {

        authService.resendOtp(request.getEmail());
        return ResponseEntity.ok("OTP resent successfully");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody LoginRequestDto loginRequestDto) {

        authService.resetPassword(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        return ResponseEntity.ok("Password reset successfully");
    }


}