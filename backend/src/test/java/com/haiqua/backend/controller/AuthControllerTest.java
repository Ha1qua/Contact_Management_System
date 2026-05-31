package com.haiqua.backend.controller;

import com.haiqua.backend.config.TestSecurityConfig;
import com.haiqua.backend.dto.*;
import com.haiqua.backend.exception.EmailAlreadyExistsException;
import com.haiqua.backend.exception.InvalidCredentialsException;
import com.haiqua.backend.exception.OtpException;
import com.haiqua.backend.exception.GlobalExceptionHandler; // ✅ Added import
import com.haiqua.backend.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haiqua.backend.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // ✅ Updated for Spring Boot 3.4+

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestSecurityConfig.class)
@WebMvcTest({AuthController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    // 🌟 ADD THIS LINE TO FIX THE CRASH
    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    // ================= REGISTER SUCCESS =================
    @Test
    void registerUserTest() throws Exception {
        UserRegistrationDto request = new UserRegistrationDto("test@gmail.com", "123456");
        UserDto response = new UserDto(1L, "test@gmail.com", false);

        when(authService.registerUser(any(UserRegistrationDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.email").value("test@gmail.com"));

        verify(authService, times(1)).registerUser(any(UserRegistrationDto.class));
    }

    // ================= REGISTER FAILURE =================
    @Test
    void registerUserFailureTest() throws Exception {
        UserRegistrationDto request = new UserRegistrationDto("", "123456");

        when(authService.registerUser(any())).thenThrow(new EmailAlreadyExistsException("Email already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                // ✅ FIXED: Now checks GlobalExceptionHandler's structured output instead of breaking
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    // ================= LOGIN SUCCESS =================
    @Test
    void loginTest() throws Exception {
        LoginRequestDto request = new LoginRequestDto("test@gmail.com", "123456");
        LoginResponseDto response = new LoginResponseDto("jwt-token");

        when(authService.loginUser(any(LoginRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                // ✅ FIXED: Added validation checks for the outer ApiResponse envelope
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.token").value("jwt-token"));

        verify(authService).loginUser(any(LoginRequestDto.class));
    }

    // ================= LOGIN FAILURE =================
    @Test
    void loginFailureTest() throws Exception {
        LoginRequestDto request = new LoginRequestDto("test@gmail.com", "wrong");

        when(authService.loginUser(any())).thenThrow(new InvalidCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    // ================= VERIFY OTP =================
    @Test
    void verifyOtpTest() throws Exception {
        VerifyOtpDto request = new VerifyOtpDto("test@gmail.com", "123456");

        // ✅ FIXED: Using explicit string matchers to align cleanly with your service definitions
        doNothing().when(authService).verifyOtp(anyString(), anyString());

        mockMvc.perform(post("/api/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OTP verified successfully"));

        verify(authService).verifyOtp(anyString(), anyString());
    }

    // ================= VERIFY OTP FAILURE =================
    @Test
    void verifyOtpFailureTest() throws Exception {
        VerifyOtpDto request = new VerifyOtpDto("test@gmail.com", "000000");

        doThrow(new OtpException("Invalid OTP"))
                .when(authService).verifyOtp(anyString(), anyString());

        mockMvc.perform(post("/api/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid OTP"));
    }

    // ================= RESEND OTP =================
    @Test
    void resendOtpTest() throws Exception {
        ResendOtpRequest request = new ResendOtpRequest("test@gmail.com");

        doNothing().when(authService).resendOtp(anyString());

        mockMvc.perform(post("/api/auth/resend-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP resent successfully"));

        verify(authService).resendOtp(anyString());
    }

    // ================= RESET PASSWORD =================
    @Test
    void resetPasswordTest() throws Exception {
        doNothing().when(authService).resetPassword(anyString(), anyString());

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        // ✅ FIXED: Key changed from "newPassword" to "password" to properly bind LoginRequestDto
                        .content("""
                                {"email":"test@gmail.com","password":"123456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset successfully"));

        verify(authService).resetPassword(anyString(), anyString());
    }

    // ================= RESET PASSWORD FAILURE =================
    @Test
    void resetPasswordFailureTest() throws Exception {
        doThrow(new OtpException("OTP not verified for reset"))
                .when(authService).resetPassword(anyString(), anyString());

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        // ✅ FIXED: Key property alignment fix
                        .content("""
                                {"email":"test@gmail.com","password":"123456"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("OTP not verified for reset"));
    }
}