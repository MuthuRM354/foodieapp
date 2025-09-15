package com.foodieapp.user.service.auth;

import com.foodieapp.user.dto.request.AuthRequest;
import com.foodieapp.user.dto.request.RefreshTokenRequest;
import com.foodieapp.user.dto.response.ApiResponse.AuthResponse;
import com.foodieapp.user.model.User;

public interface AuthenticationService {
    /**
     * Authenticate user with credentials
     */
    AuthResponse login(AuthRequest request);

    /**
     * Authenticate user with credentials and remember me option
     */
    AuthResponse login(AuthRequest request, boolean rememberMe);

    /**
     * Refresh an access token using a refresh token
     */
    AuthResponse refreshToken(RefreshTokenRequest request);

    /**
     * Logout a user
     */
    void logout(String token, String sessionId);

    /**
     * Find user by ID
     */
    User findUserById(String userId);
}
