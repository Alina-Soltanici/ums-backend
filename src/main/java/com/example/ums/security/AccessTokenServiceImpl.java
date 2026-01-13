package com.example.ums.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor

@Service
public class AccessTokenServiceImpl implements AccessTokenService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access.expirationMs}")
    private int jwtExpirationMs;
    private SecretKey key;


    @PostConstruct
    public void init() {
        System.out.println("JWT_SECRET length = " +
                (jwtSecret == null ? "NULL" : jwtSecret.length()));

        if (jwtSecret == null || jwtSecret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET invalid");
        }

        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }


    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());
        claims.put("type", "ACCESS");
        return Jwts
                .builder()
                .claims()
                .subject(userDetails.getUsername())
                .add(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .and()
                .signWith(key)
                .compact();
    }


    @Override
    public String extractUsernameFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    @Override
    public Long extractExpirationFromToken(String token){
        Claims claims = extractAllClaims(token);
        Date expiration = claims.getExpiration();
        return expiration.getTime();
    }


    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractUsernameFromToken(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    @Override
    public boolean isTokenExpired(String token){
        Claims claims = extractAllClaims(token);
        return claims.getExpiration().before(new Date());
    }

    @Override
    public Claims extractAllClaims(String token){
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
