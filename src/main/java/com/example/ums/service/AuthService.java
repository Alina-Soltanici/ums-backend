package com.example.ums.service;


import com.example.ums.dto.*;

public interface AuthService {
    RegisterResponse register(RegisterRequest registerRequest);
    LoginResponse login(LoginRequest loginRequest);
    RefreshTokenResponse refresh(RefreshTokenRequest refreshTokenRequest);
}
