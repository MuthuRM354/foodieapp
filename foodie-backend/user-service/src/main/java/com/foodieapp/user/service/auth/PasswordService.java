package com.foodieapp.user.service.auth;

import com.foodieapp.user.dto.response.ApiResponse;

public interface PasswordService {
    /**
     * Initiate the password reset process by sending a reset link
     * @param email User's email
     * @return ApiResponse with success message
     */
    ApiResponse<Void> initiatePasswordReset(String email);

    /**
     * Verify OTP and reset the password (original method)
     * @param token Password reset token
     * @param otp OTP code
     * @param newPassword New password
     * @return ApiResponse with success message
     */
    ApiResponse<Void> verifyOtpAndResetPassword(String token, String otp, String newPassword);

    /**
     * Reset password using the token from the reset link (new method)
     * @param token Reset token received via email
     * @param newPassword New password
     * @param confirmPassword Confirmation of new password
     * @return ApiResponse with success message
     */
    ApiResponse<Void> resetPassword(String token, String newPassword, String confirmPassword);

    /**
     * Change password for a user
     * @param userId User ID
     * @param currentPassword Current password
     * @param newPassword New password
     */
    void changePassword(String userId, String currentPassword, String newPassword);
}
