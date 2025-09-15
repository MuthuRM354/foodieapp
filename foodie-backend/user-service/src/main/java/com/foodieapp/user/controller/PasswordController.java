package com.foodieapp.user.controller;

import com.foodieapp.user.dto.request.PasswordResetRequest;
import com.foodieapp.user.dto.response.ApiResponse;
import com.foodieapp.user.service.auth.PasswordService;
import com.foodieapp.user.util.ResponseUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/password")
public class PasswordController {

    private static final Logger logger = LoggerFactory.getLogger(PasswordController.class);
    private final PasswordService passwordService;

    @Autowired
    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @PostMapping("/reset-request")
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(
            @Validated(PasswordResetRequest.InitialReset.class) @RequestBody PasswordResetRequest request) {
        logger.info("Password reset request received for email: {}", request.getEmail());

        ApiResponse<Void> response = passwordService.initiatePasswordReset(request.getEmail());
        return ResponseUtil.success(response.getMessage());
    }

    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Validated(PasswordResetRequest.Reset.class) @RequestBody PasswordResetRequest request) {
        logger.info("Password reset received for token: {}", request.getToken());

        ApiResponse<Void> response = passwordService.resetPassword(
                request.getToken(),
                request.getNewPassword(),
                request.getConfirmPassword());

        return ResponseUtil.success(response.getMessage());
    }
}
