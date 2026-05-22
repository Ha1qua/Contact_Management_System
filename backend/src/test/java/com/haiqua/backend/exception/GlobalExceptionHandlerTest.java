package com.haiqua.backend.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void otpException_shouldReturnBadRequest() throws Exception {

        mockMvc.perform(get("/test/otp"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid OTP"));
    }

    @Test
    void userNotFound_shouldReturn404() throws Exception {

        mockMvc.perform(get("/test/user"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void emailExists_shouldReturn400() throws Exception {

        mockMvc.perform(get("/test/email"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email exists"));
    }

    @Test
    void invalidCredentials_shouldReturn401() throws Exception {

        mockMvc.perform(get("/test/credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }
}