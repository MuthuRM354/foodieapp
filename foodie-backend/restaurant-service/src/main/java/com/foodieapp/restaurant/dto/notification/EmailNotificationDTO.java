package com.foodieapp.restaurant.dto.notification;

public class EmailNotificationDTO {
    private String to;
    private String subject;
    private String message;
    private boolean isHtml;

    // Default constructor
    public EmailNotificationDTO() {
    }

    // Constructor with all parameters
    public EmailNotificationDTO(String to, String subject, String message, boolean isHtml) {
        this.to = to;
        this.subject = subject;
        this.message = message;
        this.isHtml = isHtml;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isHtml() {
        return isHtml;
    }

    public void setHtml(boolean isHtml) {
        this.isHtml = isHtml;
    }
}
