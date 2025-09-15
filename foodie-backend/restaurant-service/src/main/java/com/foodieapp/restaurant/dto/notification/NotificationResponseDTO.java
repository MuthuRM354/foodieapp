package com.foodieapp.restaurant.dto.notification;

import java.time.LocalDateTime;

public class NotificationResponseDTO {
    private boolean success;
    private String message;
    private String recipient;
    private LocalDateTime timestamp;

    public NotificationResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
