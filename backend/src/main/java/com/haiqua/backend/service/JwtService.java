package com.haiqua.backend.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;
    private final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }


    public String generateToken(String email) {
        String token = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        logger.debug("JWT token generated successfully for email: {}", email);
        return token;
    }

    public String extractEmail(String token) {
        logger.debug("Extracting email from JWT token");

        String email =  Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        logger.debug("Email extracted from token successfully");
        return email;
    }


    public boolean validateToken(String token) {
        logger.debug("Validating JWT token");
        try {
            extractEmail(token);
            logger.debug("JWT token is valid");
            return true;
        } catch (Exception e) {
            logger.warn("Invalid JWT token detected: {}", e.getMessage());
            return false;
        }
    }

}
