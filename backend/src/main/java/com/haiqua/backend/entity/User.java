package com.haiqua.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name= "password", nullable = false)
    private String password;
    @Column(name= "otp", nullable = false)
    private String otp;
    @Column(name = "isVerified" , nullable = false)
    private boolean verified;
    @Column(name = "otpExpiry")
    private LocalDateTime otpExpiry;
    @Column(name= "otpAttempts")
    private int otpAttempts;
}
