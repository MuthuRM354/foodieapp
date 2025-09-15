package com.foodieapp.user.service.auth;

import com.foodieapp.user.dto.request.AuthRequest;
import com.foodieapp.user.dto.request.RefreshTokenRequest;
import com.foodieapp.user.dto.response.ApiResponse.AuthResponse;
import com.foodieapp.user.exception.ResourceNotFoundException;
import com.foodieapp.user.exception.TokenExpiredException;
import com.foodieapp.user.exception.UnauthorizedException;
import com.foodieapp.user.model.User;
import com.foodieapp.user.repository.UserRepository;
import com.foodieapp.user.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationServiceImpl(
            UserRepository userRepository,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        return login(request, false);
    }

    @Override
    public AuthResponse login(AuthRequest request, boolean rememberMe) {
        try {
            // Attempt authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Find user by email
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

            // Check if either email OR phone is verified (changed from email only)
            if (!user.isEmailVerified() && !user.isPhoneVerified()) {
                logger.warn("Login attempt on unverified account: {}", request.getEmail());
                throw new UnauthorizedException("Please verify either your email or phone number before logging in");
            }

            // Check if account is enabled
            if (!user.isEnabled()) {
                logger.warn("Login attempt on disabled account: {}", request.getEmail());
                throw new DisabledException("Your account has been disabled. Please contact support.");
            }

            // Check if account is locked
            if (user.isAccountLocked()) {
                logger.warn("Login attempt on locked account: {}", request.getEmail());
                throw new DisabledException("Your account has been locked due to too many failed login attempts. Please reset your password or contact support.");
            }

            // Reset failed login attempts on successful login
            if (user.getFailedLoginAttempts() > 0) {
                user.resetFailedLogins();
                userRepository.save(user);
            }

            // Generate tokens
            String accessToken = jwtService.generateToken(user);
            String refreshToken;

            if (rememberMe) {
                refreshToken = jwtService.generateRefreshToken(user);
            } else {
                // Create empty claims map for the token with shorter expiration
                Map<String, Object> claims = new HashMap<>();
                claims.put("tokenType", "refresh");
                refreshToken = jwtService.generateToken(user, claims, jwtService.getRefreshExpiration() / 2);
            }

            logger.info("User logged in successfully: {}", request.getEmail());

            // Return auth response
            return new AuthResponse(
                    accessToken,
                    refreshToken,
                    user.getId(),
                    user.getEmail(),
                    user.getUsername(),
                    user.getRole()
            );
        } catch (BadCredentialsException e) {
            // Handle wrong credentials by incrementing failed login attempts
            User user = userRepository.findByEmail(request.getEmail()).orElse(null);
            if (user != null) {
                user.recordFailedLogin();
                userRepository.save(user);

                // If account is now locked, provide different message
                if (user.isAccountLocked()) {
                    logger.warn("Account locked due to too many failed attempts: {}", request.getEmail());
                    throw new DisabledException("Your account has been locked due to too many failed login attempts. Please reset your password or contact support.");
                }
            }

            logger.warn("Failed login attempt for {}: Invalid credentials", request.getEmail());
            throw new UnauthorizedException("Invalid email or password");
        } catch (DisabledException e) {
            // Re-throw disabled exceptions
            throw e;
        } catch (AuthenticationException e) {
            logger.error("Authentication error for {}: {}", request.getEmail(), e.getMessage());
            throw new UnauthorizedException("Authentication failed: " + e.getMessage());
        }
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate refresh token
        if (!jwtService.isTokenValid(refreshToken)) {
            logger.warn("Invalid refresh token used in refresh request");
            throw new TokenExpiredException("Refresh token has expired or is invalid");
        }

        try {
            // Extract email from token
            String email = jwtService.extractUsername(refreshToken);

            // Find user by email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

            // Check if account is still active
            if (!user.isEnabled()) {
                logger.warn("Token refresh attempt for disabled account: {}", email);
                throw new UnauthorizedException("Account has been disabled");
            }

            // Generate new access token
            String newAccessToken = jwtService.generateToken(user);

            logger.info("Token refreshed successfully for: {}", email);

            // Return new tokens
            return new AuthResponse(
                    newAccessToken,
                    refreshToken, // Return the same refresh token
                    user.getId(),
                    user.getEmail(),
                    user.getUsername(),
                    user.getRole()
            );
        } catch (TokenExpiredException e) {
            logger.warn("Expired refresh token: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error during token refresh: {}", e.getMessage());
            throw new UnauthorizedException("Token refresh failed: " + e.getMessage());
        }
    }

    @Override
    public void logout(String token, String sessionId) {
        try {
            // Extract user email from token
            String email = jwtService.extractUsername(token);
            logger.info("User logged out: {}", email);

            // In a more sophisticated implementation, you could:
            // 1. Add the token to a blacklist or invalidation cache
            // 2. Clear any user-specific session data
            // 3. Notify other services about the logout
        } catch (Exception e) {
            logger.warn("Error during logout: {}", e.getMessage());
            // Don't throw exception on logout errors
        }
    }

    @Override
    public User findUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
}
