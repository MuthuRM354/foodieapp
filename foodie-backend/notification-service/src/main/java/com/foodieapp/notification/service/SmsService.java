package com.foodieapp.notification.service;

import com.foodieapp.notification.dto.SmsNotificationDTO;

public interface SmsService {
    // Change method signature to match implementation
    boolean sendSms(String to, String message);

    // Add this to handle the SmsNotificationDTO directly
    default boolean sendSms(SmsNotificationDTO notification) {
        return sendSms(notification.getTo(), notification.getMessage());
    }
}