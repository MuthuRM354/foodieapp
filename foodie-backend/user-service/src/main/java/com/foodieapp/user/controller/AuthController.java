package com.foodieapp.user.controller;

import com.foodieapp.user.dto.request.AuthRequest;
import com.foodieapp.user.dto.request.RefreshTokenRequest;
import com.foodieapp.user.dto.response.ApiResponse;
import com.foodieapp.user.model.User;
import com.foodieapp.user.security.JwtService;
import com.foodieapp.user.service.auth.AuthenticationService;
import com.foodieapp.user.service.user.UserService;
import com.foodieapp.user.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationService authService;
    private final JwtService jwtService;
    private final TokenUtil tokenUtil;
    private final UserService userService;

    @Autowired
    public AuthController(
            AuthenticationService authService,
            JwtService jwtService,
            TokenUtil tokenUtil,
            UserService userService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.tokenUtil = tokenUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<ApiResponse.AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        ApiResponse.AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<ApiResponse.AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        ApiResponse.AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String jwt) {

        // If no authorization header is present, still allow logout
        if (jwt != null && !jwt.isEmpty()) {
            try {
                String token = tokenUtil.extractToken(jwt);
                authService.logout(token, null);
            } catch (Exception e) {
                // Log the error but don't fail the logout
                logger.warn("Error during logout process: {}", e.getMessage());
            }
        }

        // Always return success - even if there's no token, we're considering the user logged out
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    /**
     * Validate token and get user information
     * Consolidates token validation and user information retrieval
     */
    @GetMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = tokenUtil.extractToken(authHeader);
            User user = tokenUtil.getUserFromToken(authHeader);

            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("roles", user.getRoleNames());
            response.put("username", user.getUsername());
            response.put("fullName", user.getFullName());
            response.put("enabled", user.isEnabled());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Consolidated user verification endpoint
     * Can verify by userId, email, or username
     */
    @GetMapping("/verify-user")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyUser(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String username) {

        Map<String, Object> result = new HashMap<>();

        try {
            User user = null;

            if (userId != null && !userId.isEmpty()) {
                user = authService.findUserById(userId);
            } else if (email != null && !email.isEmpty()) {
                user = userService.findUserByEmail(email);
            } else if (username != null && !username.isEmpty()) {
                user = userService.findUserByUsername(username);
            } else {
                return ResponseEntity.ok(ApiResponse.error(
                        "At least one parameter (userId, email, or username) is required",
                        "MISSING_PARAMETER"));
            }

            result.put("exists", true);
            result.put("userId", user.getId());
            result.put("active", user.isEnabled());
            result.put("roles", user.getRoleNames());
            result.put("email", user.getEmail());
            result.put("username", user.getUsername());
            result.put("emailVerified", user.isEmailVerified());

        } catch (Exception e) {
            result.put("exists", false);
        }

        return ResponseEntity.ok(ApiResponse.success("User verification completed", result));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<ApiResponse.AuthResponse>> verifyEmail(
            @RequestParam String token,
            @RequestParam String userId) {

        logger.info("Received email verification request for user: {}", userId);

        // Validate token and activate user
        User user = userService.verifyEmailAndActivateUser(userId, token);

        // Generate tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        ApiResponse.AuthResponse authResponse = new ApiResponse.AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole()
        );

        return ResponseEntity.ok(ApiResponse.success("Email verified successfully", authResponse));
    }
}
