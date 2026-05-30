package com.haiqua.backend.service;

import com.haiqua.backend.dto.*;
import com.haiqua.backend.entity.User;
import com.haiqua.backend.exception.InvalidCredentialsException;
import com.haiqua.backend.exception.OtpException;
import com.haiqua.backend.exception.UserNotFoundException;
import com.haiqua.backend.repository.UserRepository;
import com.haiqua.backend.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    // ================= REGISTER =================

    @Test
    void registerUserSuccessTest() {

        UserRegistrationDto dto =
                new UserRegistrationDto("test@gmail.com", "123456");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User u = invocation.getArgument(0);
                    u.setId(1L);
                    return u;
                });

        UserDto result = authService.registerUser(dto);

        assertNotNull(result);
        assertEquals(dto.getEmail(), result.getEmail());

        verify(emailService).sendOtpEmail(eq(dto.getEmail()), anyString());
    }

    @Test
    void registerUserEmailExistsTest() {

        UserRegistrationDto dto =
                new UserRegistrationDto("test@gmail.com", "123456");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> authService.registerUser(dto)
        );

        assertEquals("Email already exists", ex.getMessage());
    }

    @Test
    void registerUserEmailFailureTest() {

        UserRegistrationDto dto =
                new UserRegistrationDto("test@gmail.com", "123456");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        when(userRepository.save(any(User.class)))
                .thenReturn(new User());

        doThrow(new RuntimeException("Mail failed"))
                .when(emailService)
                .sendOtpEmail(anyString(), anyString());

        assertThrows(RuntimeException.class,
                () -> authService.registerUser(dto));
    }

    // ================= LOGIN =================

    @Test
    void loginSuccessTest() {

        LoginRequestDto dto =
                new LoginRequestDto("test@gmail.com", "123456");

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword("encoded");
        user.setVerified(true);

        when(userRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(dto.getPassword(), "encoded"))
                .thenReturn(true);

        when(jwtService.generateToken(user.getId(), dto.getEmail()))
                .thenReturn("jwt-token");
        LoginResponseDto response = authService.loginUser(dto);

        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void loginWrongPasswordTest() {

        LoginRequestDto dto =
                new LoginRequestDto("test@gmail.com", "wrong");

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword("encoded");

        when(userRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> authService.loginUser(dto));
    }

    @Test
    void loginUserNotVerifiedTest() {

        LoginRequestDto dto =
                new LoginRequestDto("test@gmail.com", "123456");

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword("encoded");
        user.setVerified(false);

        when(userRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(true);

        assertThrows(OtpException.class,
                () -> authService.loginUser(dto));
    }

    @Test
    void loginUserNotFoundTest() {

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> authService.loginUser(
                        new LoginRequestDto("a@a.com", "123")
                ));
    }

    // ================= OTP =================

    @Test
    void verifyOtpSuccessTest() {

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setOtp("123456");
        user.setOtpType("REGISTER");
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(2));
        user.setOtpAttempts(0);

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        authService.verifyOtp(user.getEmail(), "123456");

        assertTrue(user.isVerified());
        assertNull(user.getOtp());

        verify(userRepository).save(user);
    }

    @Test
    void verifyOtpInvalidTest() {

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setOtp("123456");
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(2));
        user.setOtpAttempts(0);

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        assertThrows(OtpException.class,
                () -> authService.verifyOtp(user.getEmail(), "000000"));
    }

    @Test
    void verifyOtpExpiredTest() {

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setOtp("123456");
        user.setOtpExpiry(LocalDateTime.now().minusMinutes(1));

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        assertThrows(OtpException.class,
                () -> authService.verifyOtp(user.getEmail(), "123456"));
    }

    @Test
    void verifyOtpNullTest() {

        User user = new User();
        user.setEmail("test@gmail.com");

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        assertThrows(OtpException.class,
                () -> authService.verifyOtp(user.getEmail(), "123456"));
    }

    @Test
    void verifyOtpTooManyAttemptsTest() {

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setOtp("123456");
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(2));
        user.setOtpAttempts(6);

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        assertThrows(OtpException.class,
                () -> authService.verifyOtp(user.getEmail(), "000000"));
    }

    // ================= RESEND OTP =================

    @Test
    void resendOtpSuccessTest() {

        User user = new User();
        user.setEmail("test@gmail.com");

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        authService.resendOtp(user.getEmail());

        verify(userRepository).save(user);
        verify(emailService).sendOtpEmail(eq(user.getEmail()), anyString());
    }

    @Test
    void resendOtpUserNotFoundTest() {

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> authService.resendOtp("x@gmail.com"));
    }

    // ================= RESET PASSWORD =================

    @Test
    void resetPasswordSuccessTest() {

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setOtpType("RESET_PASSWORD");

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("newPass"))
                .thenReturn("encodedPass");

        authService.resetPassword(user.getEmail(), "newPass");

        assertEquals("encodedPass", user.getPassword());
        assertNull(user.getOtpType());

        verify(userRepository).save(user);
    }

    @Test
    void resetPasswordWrongTypeTest() {

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setOtpType("REGISTER");

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class,
                () -> authService.resetPassword(user.getEmail(), "pass"));
    }

    @Test
    void resetPasswordUserNotFoundTest() {

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> authService.resetPassword("x@gmail.com", "pass"));
    }
}