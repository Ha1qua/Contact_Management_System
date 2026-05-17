package com.haiqua.backend.service.impl;

import com.haiqua.backend.dto.LoginRequestDto;
import com.haiqua.backend.dto.LoginResponseDto;
import com.haiqua.backend.dto.UserDto;
import com.haiqua.backend.dto.UserRegistrationDto;
import com.haiqua.backend.entity.User;
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

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;

    private final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    public UserDto registerUser(UserRegistrationDto registrationDto){

        logger.info("Register attempt for email: {}", registrationDto.getEmail());

        User user = UserMapper.mapToUser(registrationDto);

        String hashedPassword = passwordEncoder.encode(registrationDto.getPassword());
        user.setPassword(hashedPassword);


        String randomOtp = String.format("%06d", new java.util.Random().nextInt(1000000));
        user.setOtp(randomOtp);


        user.setVerified(false);

        User savedUser = userRepository.save(user);

        emailService.sendOtpEmail(savedUser.getEmail(), randomOtp);

        logger.info("User registered successfully: {}", savedUser.getEmail());
        return UserMapper.mapToUserDto(savedUser);

    }
    @Override
    public void verifyOtp(String email, String otp){
        logger.info("OTP verification attempt for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->{
                    logger.error("User not found during OTP verification: {}", email);
                    return new UserNotFoundException("User not found");
                });

        if (user.getOtp() == null) {
            logger.warn("OTP expired for email: {}", email);
            throw new OtpException("OTP already used or expired");
        }

        if (user.getOtp().equals(otp)) {

            user.setVerified(true);
            user.setOtp(null); // clear OTP after success

            userRepository.save(user);

            logger.info("OTP verified successfully for email: {}", email);
        } else {
            logger.warn("Invalid OTP attempt for email: {}", email);
            throw new RuntimeException("Invalid OTP");
        }
    }

    public LoginResponseDto loginUser(LoginRequestDto loginRequestDto) {

        logger.info("Login attempt for email: {}", loginRequestDto.getEmail());

        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> {
                    logger.error("User not found: {}", loginRequestDto.getEmail());
                    return new UserNotFoundException("User not found");
                });

        if (!user.isVerified()) {
            logger.warn("Unverified login attempt: {}", loginRequestDto.getEmail());
            throw new OtpException("User not verified. Please verify OTP first.");
        }
        String rawPassword = loginRequestDto.getPassword();

        logger.debug("Password validation started for email: {}", loginRequestDto.getEmail());


        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            logger.warn("Invalid password attempt for email: {}", loginRequestDto.getEmail());
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getEmail());

        logger.info("Login successful for email: {}", loginRequestDto.getEmail());

        return new LoginResponseDto(token, "Login successful");
    }
}
