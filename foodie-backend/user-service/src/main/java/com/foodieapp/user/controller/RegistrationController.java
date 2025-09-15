package com.foodieapp.user.controller;

import com.foodieapp.user.dto.request.CreateUserRequest;
import com.foodieapp.user.dto.response.ApiResponse;
import com.foodieapp.user.service.auth.RegistrationService;
import com.foodieapp.user.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/registration")
@Tag(name = "Registration", description = "API for user registration and verification")
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/customer")
    @Operation(
            summary = "Register a new customer",
            description = "Creates a new customer account and initiates verification process"
    )
    public ResponseEntity<ApiResponse<ApiResponse.AuthResponse>> registerCustomer(
            @Valid @RequestBody CreateUserRequest request,
            HttpServletRequest httpRequest) {

        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        logger.info("Customer registration request received for email: {}", request.getEmail());

        try {
            ApiResponse.AuthResponse authResponse = registrationService.registerCustomer(request);
            logger.info("Customer registration initiated for email: {}", request.getEmail());

            return ResponseUtil.success(
                    "User registration initiated. Please verify your " +
                            request.getVerificationMethod() + " to activate account.",
                    authResponse
            );
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage(), e);
            throw e; // Let GlobalExceptionHandler handle it
        } finally {
            MDC.remove("requestId");
        }
    }

    @PostMapping("/restaurant-owner")
    @Operation(
            summary = "Register a new restaurant owner",
            description = "Creates a new restaurant owner account pending admin approval"
    )
    public ResponseEntity<ApiResponse<ApiResponse.AuthResponse>> registerRestaurantOwner(
            @Valid @RequestBody CreateUserRequest request,
            HttpServletRequest httpRequest) {

        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        logger.info("Restaurant owner registration request received for email: {}", request.getEmail());

        try {
            ApiResponse.AuthResponse authResponse = registrationService.registerRestaurantOwner(request);
            logger.info("Restaurant owner registration initiated for email: {}", request.getEmail());

            return ResponseUtil.success(
                    "Restaurant owner registration initiated. Please verify your " +
                            request.getVerificationMethod() + " and wait for admin approval.",
                    authResponse
            );
        } catch (Exception e) {
            logger.error("Restaurant owner registration failed: {}", e.getMessage(), e);
            throw e; // Let GlobalExceptionHandler handle it
        } finally {
            MDC.remove("requestId");
        }
    }
}
