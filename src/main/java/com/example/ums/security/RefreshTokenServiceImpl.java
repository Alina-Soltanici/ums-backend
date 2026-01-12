package com.example.ums.security;

import com.example.ums.entity.RefreshToken;
import com.example.ums.entity.User;
import com.example.ums.exceptions.RefreshTokenNotFoundException;
import com.example.ums.exceptions.UserNotFoundException;
import com.example.ums.repository.RefreshTokenRepository;
import com.example.ums.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService{
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.refresh.expirationMs}")
    private Long tokenExpirationMs;

    private SecretKey secretKey;


    @PostConstruct
    public void init(){
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public RefreshToken generateRefreshToken(UserDetails userDetails) {
        // create unic JTI
        String jti = UUID.randomUUID().toString();

        //Create JWT token
        String token = Jwts.builder()
                .subject(userDetails.getUsername())
                .id(jti)
                .claim("type", "REFRESH")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenExpirationMs))
                .signWith(secretKey)
                .compact();

        //find user in db
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        //create refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .jti(jti)
                .user(user)
                .expirationDate(LocalDateTime.now().plusSeconds(tokenExpirationMs / 1000))
                .revoked(false)
                .build();

        //save token in db
        refreshTokenRepository.save(refreshToken);
        System.out.println("✅ Refresh token saved for user: " + user.getId());
        System.out.println("✅ JTI: " + jti);

        return refreshToken;
    }


    @Override
    public RefreshToken findByJti(String jti) {
        return refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token not found"));
    }


    @Override
    public boolean isExpired(String jti) {
        RefreshToken refreshToken = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token not found."));

        Claims claims =extractAllClaims(refreshToken.getToken());
        return claims.getExpiration().before(new Date());
    }

    @Override
    public void revokeToken(String jti) {
        RefreshToken refreshToken = refreshTokenRepository.findByJti(jti)
                .orElseThrow(()-> new RefreshTokenNotFoundException("Refresh token not found."));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

    }

    public boolean isRevoked(String jti) {
        RefreshToken refreshToken = findByJti(jti);
        return refreshToken.isRevoked();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        Claims claims = extractAllClaims(token);
        String username = claims.getSubject();
        String jti = claims.getId();

        return (username.equals(userDetails.getUsername()) && !isExpired(jti) && !isRevoked(jti));
    }

    @Override
    public String extractJtiFromToken(String token){
        Claims claims = extractAllClaims(token);
        return claims.getId();
    }

    @Override
    public void saveRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
        System.out.println("✅ Refresh token saved for user: " + refreshToken.getUser().getId());
        System.out.println("✅ JTI saved: " + refreshToken.getJti());
    }

    @Override
    public Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }
}

