package com.example.ums.service;


import com.example.ums.dto.*;
import com.example.ums.entity.*;
import com.example.ums.exceptions.CustomerNotFoundException;
import com.example.ums.exceptions.InvalidRefreshTokenException;
import com.example.ums.exceptions.RoleNotFoundException;
import com.example.ums.exceptions.UserAlreadyExistsException;
import com.example.ums.mapper.UserMapper;
import com.example.ums.repository.CustomerRepository;
import com.example.ums.repository.RefreshTokenRepository;
import com.example.ums.repository.RoleRepository;
import com.example.ums.repository.UserRepository;
import com.example.ums.security.AccessTokenService;
import com.example.ums.security.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        if(userRepository.existsByEmail (registerRequest.getEmail())) {
            log.info("Email already exists: {}", registerRequest.getEmail());
            throw new UserAlreadyExistsException("Email already taken!");
        }



        User user = new User ();

        user.setEmail (registerRequest.getEmail());
        user.setPassword (passwordEncoder.encode (registerRequest.getPassword ()));

        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow (() -> new RoleNotFoundException("Role not found!"));

        user.setRoles(Set.of (defaultRole));
        User savedUser = userRepository.save (user);


        Customer customer = new Customer();
        customer.setFirstName(registerRequest.getFirstName());
        customer.setLastName(registerRequest.getLastName());
        customer.setPhoneNumber(registerRequest.getPhoneNumber());
        customer.setAddress(registerRequest.getAddress());
        customer.setCity(registerRequest.getCity());
        customer.setUser(savedUser);

        customerRepository.save(customer);

        log.info("User registered successfully");

        UserPrincipal userPrincipal = new UserPrincipal(savedUser);
        String accessToken = accessTokenService.generateToken(userPrincipal);

        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(userPrincipal);
        refreshTokenService.saveRefreshToken(refreshToken);
        RegisterResponse response = userMapper.toDto(savedUser);
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken.getToken());
        return response;
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));


        log.info("Login request received for email: {}", loginRequest.getEmail());

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String token = accessTokenService.generateToken(userPrincipal);
        log.info("Access token issued successfully:)");
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(userPrincipal);
        refreshTokenService.saveRefreshToken(refreshToken);
        log.info("Refresh token issued successfully!");
        log.info("User logged successfully");

        assert userPrincipal != null;
        Customer customer = customerRepository.findByUserId(userPrincipal.getId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found for user " + userPrincipal.getId()));

        String role = userPrincipal.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        return LoginResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .userId(userPrincipal.getId())
                .role(role)
                .firstName(customer.getFirstName())
                .build();
    }

    @Override
    @Transactional
    public RefreshTokenResponse refresh(RefreshTokenRequest refreshTokenRequest) {
        String refreshTokenFromRequest = refreshTokenRequest.getRefreshToken();
        String jti = refreshTokenService.extractJtiFromToken(refreshTokenFromRequest);
        RefreshToken refreshToken = refreshTokenService.findByJti(jti);

        UserPrincipal userPrincipal = new UserPrincipal(refreshToken.getUser());
        if(!refreshTokenService.isTokenValid(refreshTokenFromRequest, userPrincipal)) {
            log.warn("Refresh token with jti {} is invalid", refreshToken.getJti());
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        String newAccessToken = accessTokenService.generateToken(userPrincipal);
        log.info("Issued new access token for user {}", refreshToken.getUser().getId());

        RefreshToken newRefreshToken = refreshTokenService.generateRefreshToken(userPrincipal);

        log.info("Issued new refresh token for user {}", refreshToken.getUser().getId());
        refreshTokenRepository.save(newRefreshToken);
        log.info("New refresh token for user {} was saved", newRefreshToken.getUser().getId());
        refreshTokenService.revokeToken(jti);
        log.info("Old refresh token for user {} was revoked", refreshToken.getUser().getId());

        return new RefreshTokenResponse(newAccessToken, newRefreshToken.getToken());
    }
}
