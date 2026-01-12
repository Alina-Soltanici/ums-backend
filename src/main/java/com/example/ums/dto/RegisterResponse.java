package com.example.ums.dto;

import lombok.Data;

@Data
public class RegisterResponse {
    private Long id;
    private String email;
    private String message;
    private String accessToken;
    private String refreshToken;
}
