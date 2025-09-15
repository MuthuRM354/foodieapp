package com.foodieapp.restaurant.dto.notification;

public class SmsNotificationDTO {
    private String to;
    private String message;

    // Constructor
    public SmsNotificationDTO() {
    }

    // Constructor with fields
    public SmsNotificationDTO(String to, String message) {
        this.to = to;
        this.message = message;
    }

    // Getters and setters
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
