package com.example.ums.filter;

import com.example.ums.exceptions.BlacklistedTokenException;
import com.example.ums.exceptions.InvalidTokenTypeException;
import com.example.ums.security.AccessTokenService;
import com.example.ums.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final AccessTokenService accessTokenService;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);

        if(token != null) {
            handleTokenAuthentication(token, request);
        }
        filterChain.doFilter(request, response);
    }

    //extract token from Authorization header
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }


    //validate, check blacklist, set authentication
    private void handleTokenAuthentication(String token, HttpServletRequest request) throws ServletException, IOException {
        String username=accessTokenService.extractUsernameFromToken(token);

        if (tokenBlacklistService.isTokenBlackListed(token)) {
            throw new BlacklistedTokenException("Token is blacklisted");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (accessTokenService.validateToken(token, userDetails)) {
                Claims claims = accessTokenService.extractAllClaims(token);

                if (!"ACCESS".equals(claims.get("type"))) {
                    log.info("Cannot use refresh token to access secured endpoints");
                    throw new InvalidTokenTypeException("Cannot use refresh token to access secured resources");
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
    }

    @Override
    public boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth/login") || path.startsWith("/api/auth/signup");
    }

}
