package com.foodieapp.user.controller;

import com.foodieapp.user.dto.request.OtpRequest;
import com.foodieapp.user.dto.response.ApiResponse;
import com.foodieapp.user.service.user.UserService;
import com.foodieapp.user.service.verification.OtpService;
import com.foodieapp.user.util.ResponseUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/otp")
public class OtpController {

    private static final Logger logger = LoggerFactory.getLogger(OtpController.class);

    private final OtpService otpService;
    private final UserService userService;

    @Autowired
    public OtpController(OtpService otpService, UserService userService) {
        this.otpService = otpService;
        this.userService = userService;
    }

    /**
     * Send OTP via phone only
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Map<String, String>>> sendOtp(@Valid @RequestBody OtpRequest request) {
        logger.info("Received OTP request");
        Map<String, String> response = new HashMap<>();
        String method = request.getMethod();

        // Validate method is phone
        if (!"phone".equals(method)) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Method must be 'phone'. Email verification uses verification links.", "INVALID_METHOD"));
        }

        if (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Phone number is required for OTP verification"));
        }

        // Send OTP to phone
        logger.info("Sending OTP to phone: {}", request.getPhoneNumber());
        String otp = otpService.generateOtp(request.getPhoneNumber());
        otpService.sendSmsOtp(request.getPhoneNumber(), otp);

        response.put("method", "phone");
        response.put("phoneNumber", request.getPhoneNumber());
        response.put("message", "OTP sent to your phone");

        return ResponseEntity.ok(ApiResponse.success("OTP sent to phone", response));
    }

    /**
     * Verify OTP received by phone only
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Map<String, String>>> verifyOtp(@Valid @RequestBody OtpRequest request) {
        logger.info("Received OTP verification request");
        String type = request.getType();
        String otp = request.getOtp();
        String userId = request.getUserId();
        String verificationType = request.getVerificationType();

        // Validate input parameters
        if (type == null || otp == null) {
            logger.warn("Missing required OTP verification parameters: type={}, otp={}", type, otp);
            return ResponseUtil.error("Type and OTP are required fields", "VALIDATION_ERROR");
        }

        if (!"phone".equals(type)) {
            return ResponseUtil.error("Only phone verification is supported through OTP. Email verification uses verification links.", "INVALID_TYPE");
        }

        try {
            Map<String, String> response = new HashMap<>();

            // Handle registration-specific verification if userId is provided
            if (userId != null && !userId.isEmpty() && "registration".equals(verificationType)) {
                logger.info("Processing registration OTP verification for user: {}", userId);
                // This would be delegated to registration service in a real implementation
                response.put("userId", userId);
                response.put("verificationType", "registration");
                response.put("verified", "true");

                // Verify the OTP
                if ("phone".equals(type) && request.getPhoneNumber() != null) {
                    boolean valid = otpService.validateOtp(request.getPhoneNumber(), otp);
                    if (!valid) {
                        throw new IllegalArgumentException("Invalid OTP");
                    }
                    otpService.clearOtp(request.getPhoneNumber());

                    // Update user verification status
                    userService.updateVerificationStatus("phone", request.getPhoneNumber());
                }

                return ResponseUtil.success("Phone verification successful", response);
            }
            // Handle standard phone verification
            else if ("phone".equals(type) && request.getPhoneNumber() != null) {
                // Phone verification
                response = verifyOtpAndUpdateUser(request.getPhoneNumber(), "phone", otp);
                return ResponseUtil.success("Phone verified successfully", response);
            }
            else {
                logger.warn("Invalid verification type or missing data. Type: {}, Phone: {}",
                        type, request.getPhoneNumber());
                return ResponseUtil.error("Invalid verification type or missing required information. " +
                                "Please provide a valid phone number.",
                        "INVALID_VERIFICATION_TYPE");
            }
        } catch (IllegalArgumentException e) {
            logger.warn("OTP verification failed: {}", e.getMessage());
            return ResponseUtil.error(e.getMessage(), "INVALID_OTP");
        } catch (Exception e) {
            logger.error("Error during OTP verification: ", e);
            return ResponseUtil.error("An error occurred during verification. Please try again.", "VERIFICATION_ERROR");
        }
    }

    /**
     * Helper method to verify OTP and update user verification status
     */
    private Map<String, String> verifyOtpAndUpdateUser(String identifier, String type, String otp) {
        logger.info("Verifying OTP for {}: {}", type, identifier);
        Map<String, String> response = new HashMap<>();

        boolean isValid = otpService.validateOtp(identifier, otp);
        if (!isValid) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        userService.updateVerificationStatus(type, identifier);
        otpService.clearOtp(identifier);

        response.put("method", type);
        response.put(type, identifier);
        response.put("verified", "true");

        return response;
    }
}
