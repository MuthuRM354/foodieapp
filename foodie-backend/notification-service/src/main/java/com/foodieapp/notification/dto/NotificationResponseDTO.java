package com.foodieapp.notification.dto;

import java.time.LocalDateTime;

public class NotificationResponseDTO {
    private boolean success;
    private String message;
    private Object data;
    private String recipient;  // Added recipient field
    private LocalDateTime timestamp;  // Added timestamp field
    private String status; // Added status field

    // Default constructor
    public NotificationResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    // Constructor with basic fields
    public NotificationResponseDTO(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // Full constructor
    public NotificationResponseDTO(boolean success, String message, Object data, String recipient, LocalDateTime timestamp, String status) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.recipient = recipient;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.status = status;
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Static builder method
    public static Builder builder() {
        return new Builder();
    }

    // Builder class implementation
    public static class Builder {
        private boolean success;
        private String message;
        private Object data;
        private String recipient;
        private LocalDateTime timestamp = LocalDateTime.now();
        private String status; // Added status field to builder

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public Builder recipient(String recipient) {
            this.recipient = recipient;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        // Add the missing status method
        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public NotificationResponseDTO build() {
            return new NotificationResponseDTO(success, message, data, recipient, timestamp, status);
        }
    }
}