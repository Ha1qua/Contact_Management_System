package com.haiqua.backend.controller;

import com.haiqua.backend.exception.*;
        import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestExceptionController {

    @GetMapping("/otp")
    public void otp() {
        throw new OtpException("Invalid OTP");
    }

    @GetMapping("/user")
    public void user() {
        throw new UserNotFoundException("User not found");
    }

    @GetMapping("/email")
    public void email() {
        throw new EmailAlreadyExistsException("Email exists");
    }

    @GetMapping("/credentials")
    public void credentials() {
        throw new InvalidCredentialsException("Invalid credentials");
    }
}