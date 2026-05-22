package com.haiqua.backend.controller;

import com.haiqua.backend.config.TestSecurityConfig;
import com.haiqua.backend.dto.*;
import com.haiqua.backend.exception.EmailAlreadyExistsException;
import com.haiqua.backend.exception.InvalidCredentialsException;
import com.haiqua.backend.exception.OtpException;
import com.haiqua.backend.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Import(TestSecurityConfig.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    // ================= REGISTER SUCCESS =================
    @Test
    void registerUserTest() throws Exception {

        // 1. Request DTO
        UserRegistrationDto request =
                new UserRegistrationDto("test@gmail.com", "123456");

        // 2. Service response (mocked)
        UserDto response =
                new UserDto(1L, "test@gmail.com", false);

        // 3. Mock service call
        when(authService.registerUser(any(UserRegistrationDto.class)))
                .thenReturn(response);

        // 4. Perform request + verify response
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.email").value("test@gmail.com"));

        // 5. Verify service call
        verify(authService, times(1)).registerUser(any(UserRegistrationDto.class));
    }

    // ================= REGISTER FAILURE =================
    @Test
    void registerUserFailureTest() throws Exception {

        UserRegistrationDto request =
                new UserRegistrationDto("", "123456");

        when(authService.registerUser(any()))
                .thenThrow(new EmailAlreadyExistsException("Email already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ================= LOGIN SUCCESS =================
    @Test
    void loginTest() throws Exception {

        LoginRequestDto request =
                new LoginRequestDto("test@gmail.com", "123456");

        LoginResponseDto response =
                new LoginResponseDto("jwt-token");

        when(authService.loginUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("jwt-token"));

        verify(authService).loginUser(any());
    }

    // ================= LOGIN FAILURE =================
    @Test
    void loginFailureTest() throws Exception {

        LoginRequestDto request =
                new LoginRequestDto("test@gmail.com", "wrong");

        when(authService.loginUser(any()))
                .thenThrow(new InvalidCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // ================= VERIFY OTP =================
    @Test
    void verifyOtpTest() throws Exception {

        VerifyOtpDto request =
                new VerifyOtpDto("test@gmail.com", "123456");

        doNothing().when(authService).verifyOtp(any(), any());

        mockMvc.perform(post("/api/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP verified successfully"));

        verify(authService).verifyOtp(any(), any());
    }

    // ================= VERIFY OTP FAILURE =================
    @Test
    void verifyOtpFailureTest() throws Exception {

        VerifyOtpDto request =
                new VerifyOtpDto("test@gmail.com", "000000");

        doThrow(new OtpException("Invalid OTP"))
                .when(authService).verifyOtp(any(), any());

        mockMvc.perform(post("/api/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ================= RESEND OTP =================
    @Test
    void resendOtpTest() throws Exception {

        ResendOtpRequest request =
                new ResendOtpRequest("test@gmail.com");

        doNothing().when(authService).resendOtp(any());

        mockMvc.perform(post("/api/auth/resend-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP resent successfully"));

        verify(authService).resendOtp(any());
    }

    // ================= RESET PASSWORD =================
    @Test
    void resetPasswordTest() throws Exception {

        doNothing().when(authService)
                .resetPassword(any(), any());

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"test@gmail.com","newPassword":"123456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset successfully"));

        verify(authService).resetPassword(any(), any());
    }

    // ================= RESET PASSWORD FAILURE =================
    @Test
    void resetPasswordFailureTest() throws Exception {

        doThrow(new OtpException("OTP not verified for reset"))
                .when(authService)
                .resetPassword(any(), any());

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"test@gmail.com","newPassword":"123456"}
                                """))
                .andExpect(status().isBadRequest());
    }
}