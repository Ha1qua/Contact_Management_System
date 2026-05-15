package com.haiqua.backend.controller;

import com.haiqua.backend.dto.VerfiyOtpDto;
import com.haiqua.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {

    private final UserService userService;
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerfiyOtpDto verfiyOtpDto){
        userService.verifyOtp(verfiyOtpDto.getEmail(),verfiyOtpDto.getOtp());

        return ResponseEntity.ok("OTP Verified Successfully");
    }
}
