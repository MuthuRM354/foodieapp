package com.foodieapp.user.service.verification;

import java.util.Set;

public interface OtpService {
    String generateOtp(String identifier);
    void sendEmailOtp(String email, String otp);
    void sendSmsOtp(String phoneNumber, String otp);
    boolean validateOtp(String identifier, String otp);
    void clearOtp(String identifier);
    Set<String> getAllActiveKeys();
}
