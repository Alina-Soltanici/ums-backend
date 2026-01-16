package com.example.ums.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class LoginResponse {
    private Long id;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long userId;
    private List<String> roles;
    private String firstName;
}
