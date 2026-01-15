package com.example.ums.controller;

import com.example.ums.dto.*;
import com.example.ums.security.AccessTokenService;
import com.example.ums.security.RefreshTokenService;
import com.example.ums.service.AuthService;
import com.example.ums.service.TokenBlacklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final TokenBlacklistService blacklistService;
    private final RefreshTokenService refreshTokenService;
    private final AccessTokenService accessTokenService;

    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        return new ResponseEntity<>(authService.refresh(request), HttpStatus.OK);
    }


    @PostMapping("/auth/signup")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return new ResponseEntity<> (authService.register (registerRequest), HttpStatus.CREATED);
    }


    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(authService.login(loginRequest), HttpStatus.OK);
    }


    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam("refreshToken") String refreshToken) {

        log.info("Logout request received");


        String jti = refreshTokenService.extractJtiFromToken(refreshToken);
        refreshTokenService.revokeToken(jti);
        log.info("Refresh token revoked");


        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String accessToken = authHeader.substring(7);

                Long expirationTime = accessTokenService.extractExpirationFromToken(accessToken);
                long remainingSeconds = (expirationTime - System.currentTimeMillis()) / 1000;

                if (remainingSeconds > 0) {
                    blacklistService.blacklistToken(accessToken, remainingSeconds);
                    log.info("Access token blacklisted for {} seconds", remainingSeconds);
                }
            } catch (Exception e) {
                log.warn("Access token invalid or expired, skipping blacklist");
            }
        }

        return ResponseEntity.ok("Logout successful");
    }



    @GetMapping("/user/secure")
    public ResponseEntity<String>getUserPage() {
        return new ResponseEntity<>("User page", HttpStatus.OK);
    }


    @GetMapping("/admin/secure")
    public ResponseEntity<String> getAdminPage() {
        return new ResponseEntity<>("Admin page", HttpStatus.OK);
    }
}