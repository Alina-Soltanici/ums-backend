package com.example.ums.dto;

import lombok.Data;

import java.util.List;

@Data
public class RegisterResponse {
    private Long id;
    private String email;
    private String message;
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private List<String> roles;
}
