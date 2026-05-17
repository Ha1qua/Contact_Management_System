package com.haiqua.backend.service.impl;

import com.haiqua.backend.dto.LoginRequestDto;
import com.haiqua.backend.dto.LoginResponseDto;
import com.haiqua.backend.dto.UserDto;
import com.haiqua.backend.dto.UserRegistrationDto;
import com.haiqua.backend.entity.User;
import com.haiqua.backend.mapper.UserMapper;
import com.haiqua.backend.repository.UserRepository;
import com.haiqua.backend.service.AuthService;
import com.haiqua.backend.service.EmailService;
import com.haiqua.backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;

    @Override
    public UserDto registerUser(UserRegistrationDto registrationDto){


        User user = UserMapper.mapToUser(registrationDto);


        String hashedPassword = passwordEncoder.encode(registrationDto.getPassword());
        user.setPassword(hashedPassword);


        String randomOtp = String.format("%06d", new java.util.Random().nextInt(1000000));
        user.setOtp(randomOtp);


        user.setVerified(false);

        User savedUser = userRepository.save(user);

        emailService.sendOtpEmail(savedUser.getEmail(), randomOtp);


        return UserMapper.mapToUserDto(savedUser);

    }
    @Override
    public void verifyOtp(String email, String otp){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getOtp() == null) {
            throw new RuntimeException("OTP already used or expired");
        }

        if (user.getOtp().equals(otp)) {

            user.setVerified(true);
            user.setOtp(null); // clear OTP after success

            userRepository.save(user);

        } else {
            throw new RuntimeException("Invalid OTP");
        }
    }

    public LoginResponseDto loginUser(LoginRequestDto loginRequestDto) {

        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isVerified()) {
            throw new RuntimeException("User not verified. Please verify OTP first.");
        }
        String rawPassword = loginRequestDto.getPassword();

        System.out.println("RAW=[" + rawPassword + "]");
        System.out.println("LENGTH=" + rawPassword.length());

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }





        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponseDto(token, "Login successful");
    }
}
