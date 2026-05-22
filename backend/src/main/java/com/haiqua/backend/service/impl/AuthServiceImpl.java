package com.haiqua.backend.service.impl;

import com.haiqua.backend.dto.LoginRequestDto;
import com.haiqua.backend.dto.LoginResponseDto;
import com.haiqua.backend.dto.UserDto;
import com.haiqua.backend.dto.UserRegistrationDto;
import com.haiqua.backend.entity.User;
import com.haiqua.backend.exception.EmailAlreadyExistsException;
import com.haiqua.backend.exception.InvalidCredentialsException;
import com.haiqua.backend.exception.OtpException;
import com.haiqua.backend.exception.UserNotFoundException;
import com.haiqua.backend.mapper.UserMapper;
import com.haiqua.backend.repository.UserRepository;
import com.haiqua.backend.service.AuthService;
import com.haiqua.backend.service.EmailService;
import com.haiqua.backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;
    private LocalDateTime otpExpiry;
    private final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    public UserDto registerUser(UserRegistrationDto registrationDto){

        logger.info("Register attempt for email: {}", registrationDto.getEmail());
        if(userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = UserMapper.mapToUser(registrationDto);

        String hashedPassword = passwordEncoder.encode(registrationDto.getPassword());
        user.setPassword(hashedPassword);


        String randomOtp = String.format("%06d", new java.util.Random().nextInt(1000000));
        user.setOtp(randomOtp);
        user.setOtpType("REGISTER");
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(2));
        user.setVerified(false);

        User savedUser = userRepository.save(user);


        emailService.sendOtpEmail(savedUser.getEmail(), randomOtp);

        logger.info("User registered successfully: {}", savedUser.getEmail());
        return UserMapper.mapToUserDto(savedUser);

    }

    @Override
    public void verifyOtp(String email, String otp) {

        logger.info("OTP verification attempt for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));


        if (user.getOtp() == null || user.getOtpExpiry() == null) {
            throw new OtpException("OTP already used or not generated");
        }


        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new OtpException("OTP expired");
        }


        if (!user.getOtp().equals(otp)) {

            user.setOtpAttempts(user.getOtpAttempts() + 1);

            if (user.getOtpAttempts() > 5) {
                userRepository.save(user);
                throw new OtpException("Too many attempts");
            }

            userRepository.save(user);
            throw new OtpException("Invalid OTP");
        }


        if ("REGISTER".equals(user.getOtpType())) {
            user.setVerified(true);
        }

        user.setOtp(null);
        user.setOtpType("RESET_PASSWORD");
        user.setOtpAttempts(0);
        user.setOtpExpiry(null);

        userRepository.save(user);

        logger.info("OTP verified successfully for email: {}", email);
    }

    public LoginResponseDto loginUser(LoginRequestDto loginRequestDto) {

        logger.info("Login attempt for email: {}", loginRequestDto.getEmail());

        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> {
                    logger.error("User not found: {}", loginRequestDto.getEmail());
                    return new UserNotFoundException("User not found");
                });

        boolean isPasswordValid = passwordEncoder.matches(
                loginRequestDto.getPassword(),
                user.getPassword()
        );

        if (!isPasswordValid) {
            logger.warn("Invalid password attempt for email: {}", loginRequestDto.getEmail());
            throw new InvalidCredentialsException("Invalid credentials");
        }

        if (!user.isVerified()) {
            logger.warn("Unverified login attempt: {}", loginRequestDto.getEmail());
            throw new OtpException("User not verified. Please verify OTP first.");
        }

        String token = jwtService.generateToken(user.getEmail());

        logger.info("Login successful for email: {}", loginRequestDto.getEmail());

        return new LoginResponseDto(token);
    }
    @Override
    public void resendOtp(String email) {

        logger.info("Resend OTP request initiated for email: {}", email);

        try {

            logger.debug("Searching user in database for email: {}", email);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.error("User not found for email: {}", email);
                        return new UserNotFoundException("User not found with email: " + email);
                    });

            logger.info("User found: {}", user.getEmail());

            // generate new OTP
            String newOtp = String.format("%06d", new java.util.Random().nextInt(1000000));

            logger.debug("Generated new OTP for email {}: {}", email, newOtp);

            // update user
            user.setOtp(newOtp);
            user.setOtpExpiry(LocalDateTime.now().plusMinutes(2));

            userRepository.save(user);

            logger.info("OTP updated in database for email: {}", email);

            // send email
            emailService.sendOtpEmail(email, newOtp);

            logger.info("OTP email sent successfully to: {}", email);

        } catch (UserNotFoundException ex) {

            logger.warn("Resend OTP failed - user not found: {}", email);
            throw ex;

        } catch (Exception ex) {

            logger.error("Unexpected error while resending OTP for email: {}", email, ex);
            throw new RuntimeException("Failed to resend OTP. Please try again later.");
        }
    }

    @Override
    public void resetPassword(String email, String newPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!"RESET_PASSWORD".equals(user.getOtpType())) {
            throw new OtpException("OTP not verified for reset");
        }
        user.setPassword(passwordEncoder.encode(newPassword));

        user.setOtp(null);
        user.setOtpType(null);
        user.setOtpExpiry(null);
        user.setOtpAttempts(0);
        userRepository.save(user);
    }
}
