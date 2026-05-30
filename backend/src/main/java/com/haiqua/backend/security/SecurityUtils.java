package com.haiqua.backend.security;

import com.haiqua.backend.entity.User;
import com.haiqua.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private final UserRepository userRepository;

    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long getCurrentUserId() {

        String userIdStr = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return Long.parseLong(userIdStr);
    }

    public User getLoggedInUser() {

        Long userId = getCurrentUserId();

        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}