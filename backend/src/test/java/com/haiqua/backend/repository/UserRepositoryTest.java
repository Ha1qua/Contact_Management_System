package com.haiqua.backend.repository;

import com.haiqua.backend.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // 🌟 Automatically discovers H2 on the classpath now!
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    // ================= FIND BY EMAIL SUCCESS =================
    @Test
    void findByEmail_shouldReturnUser_whenUserExists() {
        // Arrange
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("password123");

        entityManager.persist(user);
        entityManager.flush();

        // Act
        Optional<User> foundUser = userRepository.findByEmail("test@gmail.com");

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@gmail.com");
    }

    // ================= FIND BY EMAIL NOT FOUND =================
    @Test
    void findByEmail_shouldReturnEmpty_whenUserDoesNotExist() {
        // Act
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@gmail.com");

        // Assert
        assertThat(foundUser).isEmpty();
    }

    // ================= EXISTS BY EMAIL =================
    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        // Arrange
        User user = new User();
        user.setEmail("unique@gmail.com");
        user.setPassword("secure");

        entityManager.persist(user);
        entityManager.flush();

        // Act
        boolean exists = userRepository.existsByEmail("unique@gmail.com");
        boolean doesNotExist = userRepository.existsByEmail("wrong@gmail.com");

        // Assert
        assertThat(exists).isTrue();
        assertThat(doesNotExist).isFalse();
    }
}