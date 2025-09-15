package com.foodieapp.user.service.verification;

import com.foodieapp.user.client.NotificationServiceClient;
import com.foodieapp.user.service.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class OtpServiceImpl implements OtpService {
    private final NotificationServiceClient notificationClient;
    private static final Logger logger = LoggerFactory.getLogger(OtpServiceImpl.class);
    private static final long OTP_VALID_DURATION = 5 * 60 * 1000; // 5 minutes in milliseconds

    // Store OTPs with their creation timestamps
    private final Map<String, OtpData> otpMap = new ConcurrentHashMap<>();
    private final Random random = new SecureRandom();
    private final EmailService emailService;
    private final Environment environment;

    @Value("${app.sms.enabled:false}")
    private boolean smsEnabled;

    @Value("${app.otp.length:6}")
    private int otpLength;

    @Autowired
    public OtpServiceImpl(NotificationServiceClient notificationClient, Environment environment, EmailService emailService) {
        this.notificationClient = notificationClient;
        this.environment = environment;
        this.emailService = emailService;
    }

    @Override
    public String generateOtp(String identifier) {
        String otp = generateRandomOtp(otpLength);
        otpMap.put(identifier, new OtpData(otp, System.currentTimeMillis()));
        logger.info("OTP generated for identifier: {}", identifier);
        return otp;
    }

    @Override
    public void sendEmailOtp(String email, String otp) {
        emailService.sendEmailOtp(email, otp, "Foodie App - Your Verification Code",
                "Your verification code is: " + otp);
    }

    @Override
    public void sendSmsOtp(String phoneNumber, String otp) {
        if (!smsEnabled) {
            logger.info("============= SMS SERVICE DISABLED =============");
            logger.info("Would have sent SMS OTP {} to {}", otp, phoneNumber);
            logger.info("Your verification code is: {}", otp);
            logger.info("This code will expire in 5 minutes.");
            logger.info("===============================================");
            return;
        }

        // Use notification service to send the actual SMS
        notificationClient.sendSmsOtp(phoneNumber, otp);
    }

    @Override
    public boolean validateOtp(String identifier, String otp) {
        OtpData storedOtpData = otpMap.get(identifier);

        if (storedOtpData == null) {
            logger.warn("No OTP found for identifier: {}", identifier);
            return false;
        }

        // Check if OTP is expired
        if (System.currentTimeMillis() - storedOtpData.timestamp > OTP_VALID_DURATION) {
            logger.warn("OTP expired for identifier: {}", identifier);
            otpMap.remove(identifier);
            return false;
        }

        // Check if OTP matches
        boolean isValid = storedOtpData.otp.equals(otp);
        if (isValid) {
            logger.info("OTP validated successfully for identifier: {}", identifier);
        } else {
            logger.warn("Invalid OTP provided for identifier: {}", identifier);
        }

        return isValid;
    }

    @Override
    public void clearOtp(String identifier) {
        otpMap.remove(identifier);
        logger.info("OTP cleared for identifier: {}", identifier);
    }

    @Override
    public Set<String> getAllActiveKeys() {
        long currentTime = System.currentTimeMillis();
        return otpMap.entrySet().stream()
                .filter(e -> currentTime - e.getValue().timestamp <= OTP_VALID_DURATION)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private String generateRandomOtp(int length) {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    private static class OtpData {
        private final String otp;
        private final long timestamp;

        public OtpData(String otp, long timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }
    }
}
