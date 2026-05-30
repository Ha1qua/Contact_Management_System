package com.haiqua.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private final Logger logger = LoggerFactory.getLogger(JwtService.class);

    // signing key
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // GENERATE TOKEN
    public String generateToken(Long userId, String email) {

        String token = Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 1000 * 60 * 60)
                )
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        logger.debug("JWT token generated for user: {}", email);

        return token;
    }

    // EXTRACT ALL CLAIMS
    private Claims extractClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // EXTRACT EMAIL
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // EXTRACT USER ID
    public Long extractUserId(String token) {

        Integer userId = extractClaims(token)
                .get("userId", Integer.class);

        return userId.longValue();
    }

    // VALIDATE TOKEN
    public boolean validateToken(String token) {

        try {

            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            return true;

        } catch (ExpiredJwtException e) {

            logger.warn("JWT token expired: {}", e.getMessage());
            return false;

        } catch (Exception e) {

            logger.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}