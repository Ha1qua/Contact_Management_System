package com.haiqua.backend.service;

public interface EmailService {
    void sendOtpEmail(String email, String otp);
}
