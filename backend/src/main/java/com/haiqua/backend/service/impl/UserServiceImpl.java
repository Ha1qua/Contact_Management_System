package com.haiqua.backend.service.impl;

import com.haiqua.backend.dto.UserDto;
import com.haiqua.backend.dto.UserRegistrationDto;
import com.haiqua.backend.entity.User;
import com.haiqua.backend.mapper.UserMapper;
import com.haiqua.backend.repository.UserRepository;
import com.haiqua.backend.service.EmailService;
import com.haiqua.backend.service.UserService;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
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
}


