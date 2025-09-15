package com.foodieapp.user.service.auth;

import com.foodieapp.user.dto.request.CreateUserRequest;
import com.foodieapp.user.dto.request.OtpRequest;
import com.foodieapp.user.dto.response.ApiResponse.AuthResponse;

public interface RegistrationService {
    /**
     * Register a new customer
     */
    AuthResponse registerCustomer(CreateUserRequest request);

    /**
     * Register a new restaurant owner
     */
    AuthResponse registerRestaurantOwner(CreateUserRequest request);

    /**
     * Register a new admin user
     */
    AuthResponse registerAdmin(CreateUserRequest request);

    /**
     * Verify OTP for registration
     */
    AuthResponse verifyRegistrationOtp(OtpRequest request);
}
