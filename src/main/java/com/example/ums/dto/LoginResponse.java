package com.example.ums.dto;

import lombok.Data;


@Data
public class LoginResponse {
    private Long id;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long userId;
    private String role;
    private String firstName;
}
