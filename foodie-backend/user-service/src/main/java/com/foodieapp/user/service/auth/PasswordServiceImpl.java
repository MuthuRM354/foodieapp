package com.foodieapp.user.service.auth;

import com.foodieapp.user.dto.response.ApiResponse;
import com.foodieapp.user.dto.response.NotificationResponseDTO;
import com.foodieapp.user.exception.ResourceNotFoundException;
import com.foodieapp.user.exception.UnauthorizedException;
import com.foodieapp.user.exception.ValidationException;
import com.foodieapp.user.model.User;
import com.foodieapp.user.repository.UserRepository;
import com.foodieapp.user.service.email.EmailService;
import com.foodieapp.user.service.verification.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class PasswordServiceImpl implements PasswordService {
    private static final Logger logger = LoggerFactory.getLogger(PasswordServiceImpl.class);
    private static final long EXPIRATION_TIME = TimeUnit.MINUTES.toMillis(30); // 30 minutes

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final EmailService emailService;
    private final ConcurrentHashMap<String, ResetTokenData> tokenStore = new ConcurrentHashMap<>();

    @Autowired
    private Environment environment;

    public PasswordServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            OtpService otpService,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
        this.emailService = emailService;
    }

    @Override
    public ApiResponse<Void> initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Generate a unique token
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, new ResetTokenData(email, System.currentTimeMillis()));

        // Generate OTP for the transitional period
        String otp = otpService.generateOtp(email);

        // Send password reset email with link
        NotificationResponseDTO emailResponse = emailService.sendPasswordResetLink(email, token);

        if (!emailResponse.isSuccess()) {
            logger.error("Failed to send reset email: {}", emailResponse.getMessage());
            throw new RuntimeException("Failed to send reset instructions: " + emailResponse.getMessage());
        }

        logger.info("Password reset initiated for email: {}", email);
        return ApiResponse.success("Password reset initiated. Check your email for instructions.", null);
    }

    @Override
    @Transactional
    public ApiResponse<Void> verifyOtpAndResetPassword(String token, String otp, String newPassword) {
        // Add null checks for all parameters
        if (token == null) {
            logger.error("Password reset failed: token is null");
            throw new ValidationException("Token cannot be null");
        }

        if (otp == null) {
            logger.error("Password reset failed: OTP is null");
            throw new ValidationException("OTP cannot be null");
        }

        if (newPassword == null) {
            logger.error("Password reset failed: new password is null");
            throw new ValidationException("New password cannot be null");
        }

        ResetTokenData data = tokenStore.get(token);

        if (data == null) {
            logger.warn("Invalid or used reset token");
            throw new ResourceNotFoundException("Invalid or expired reset token");
        }

        // Check if token is expired
        if (System.currentTimeMillis() - data.timestamp > EXPIRATION_TIME) {
            tokenStore.remove(token);
            logger.warn("Reset token expired");
            throw new UnauthorizedException("Reset token has expired. Please request a new one.");
        }

        // Verify OTP
        if (!otpService.validateOtp(data.email, otp)) {
            logger.warn("Invalid OTP provided for password reset");
            throw new UnauthorizedException("Invalid OTP");
        }

        // Clear OTP after successful validation
        otpService.clearOtp(data.email);

        // Get user and update password
        User user = userRepository.findByEmail(data.email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Remove used token
        tokenStore.remove(token);

        logger.info("Password reset successful for email: {}", data.email);
        return ApiResponse.success("Password reset successful", null);
    }

    @Override
    @Transactional
    public ApiResponse<Void> resetPassword(String token, String newPassword, String confirmPassword) {
        // Validate token
        ResetTokenData data = tokenStore.get(token);
        if (data == null) {
            logger.warn("Invalid or used reset token");
            throw new ResourceNotFoundException("Invalid or expired reset token");
        }

        // Check if token is expired
        if (System.currentTimeMillis() - data.timestamp > EXPIRATION_TIME) {
            tokenStore.remove(token);
            logger.warn("Reset token expired");
            throw new UnauthorizedException("Reset token has expired. Please request a new one.");
        }

        // Verify passwords match
        if (!newPassword.equals(confirmPassword)) {
            throw new ValidationException("New password and confirmation do not match");
        }

        // Get user and update password
        User user = userRepository.findByEmail(data.email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Remove used token
        tokenStore.remove(token);

        logger.info("Password reset successful for email: {}", data.email);
        return ApiResponse.success("Password reset successful", null);
    }

    @Override
    @Transactional
    public void changePassword(String userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        logger.info("Password changed for user: {}", userId);
    }

    // Inner class to store reset token data
    private static class ResetTokenData {
        private final String email;
        private final long timestamp;

        public ResetTokenData(String email, long timestamp) {
            this.email = email;
            this.timestamp = timestamp;
        }
    }
}
