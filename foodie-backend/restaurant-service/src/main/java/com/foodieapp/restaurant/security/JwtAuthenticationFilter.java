package com.foodieapp.restaurant.security;

import com.foodieapp.restaurant.client.UserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserServiceClient userServiceClient;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, UserServiceClient userServiceClient) {
        this.jwtService = jwtService;
        this.userServiceClient = userServiceClient;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Extract token from Authorization header
            final String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Extract JWT token from header
            final String jwt = authHeader.substring(7);

            // Validate token with User Service
            Map<String, Object> tokenInfo = userServiceClient.validateToken(jwt);

            // If token is valid and user is not already authenticated
            if (Boolean.TRUE.equals(tokenInfo.get("valid")) &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                // Extract user ID from token info
                String userId = tokenInfo.get("userId").toString();

                // Extract roles/authorities
                List<SimpleGrantedAuthority> authorities = extractAuthorities(tokenInfo);

                // Create authentication token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userId, null, authorities);

                // Set details
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in Spring Security context
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Add user ID as header for services that might need it
                request.setAttribute("userId", userId);
            }
        } catch (Exception e) {
            logger.error("Error validating JWT token: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    @SuppressWarnings("unchecked")
    private List<SimpleGrantedAuthority> extractAuthorities(Map<String, Object> tokenInfo) {
        try {
            List<String> roles = (List<String>) tokenInfo.get("roles");
            if (roles != null) {
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                for (String role : roles) {
                    authorities.add(new SimpleGrantedAuthority(role));
                }
                return authorities;
            }
        } catch (Exception e) {
            logger.error("Error extracting authorities: {}", e.getMessage());
        }
        return Collections.emptyList();
    }
}
