package com.foodieapp.notification.entity;

import com.foodieapp.notification.util.NotificationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notification_logs")
public class NotificationLog {

    @Id
    private String id;
    private String userId;
    private NotificationType type;
    private String recipient;
    private String subject;
    private String content;
    private String status;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private String errorMessage;
    private boolean read;
    private String statusMessage;

    // Default constructor
    public NotificationLog() {
        this.sentAt = LocalDateTime.now();
        this.read = false;
    }

    // All-args constructor
    public NotificationLog(String id, String userId, NotificationType type, String recipient, String subject,
                          String content, String status, LocalDateTime sentAt,
                          LocalDateTime deliveredAt, String errorMessage, boolean read, String statusMessage) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
        this.status = status;
        this.sentAt = sentAt;
        this.deliveredAt = deliveredAt;
        this.errorMessage = errorMessage;
        this.read = read;
        this.statusMessage = statusMessage;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    // Static builder method
    public static NotificationLogBuilder builder() {
        return new NotificationLogBuilder();
    }

    // Updated Builder class with statusMessage field
    public static class NotificationLogBuilder {
        private String id;
        private String userId;
        private NotificationType type;
        private String recipient;
        private String subject;
        private String content;
        private String status;
        private LocalDateTime sentAt;
        private LocalDateTime deliveredAt;
        private String errorMessage;
        private boolean read;
        private String statusMessage;

        public NotificationLogBuilder() {
        }

        public NotificationLogBuilder id(String id) {
            this.id = id;
            return this;
        }

        public NotificationLogBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public NotificationLogBuilder type(NotificationType type) {
            this.type = type;
            return this;
        }

        public NotificationLogBuilder recipient(String recipient) {
            this.recipient = recipient;
            return this;
        }

        public NotificationLogBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public NotificationLogBuilder content(String content) {
            this.content = content;
            return this;
        }

        public NotificationLogBuilder status(String status) {
            this.status = status;
            return this;
        }

        public NotificationLogBuilder sentAt(LocalDateTime sentAt) {
            this.sentAt = sentAt;
            return this;
        }

        public NotificationLogBuilder deliveredAt(LocalDateTime deliveredAt) {
            this.deliveredAt = deliveredAt;
            return this;
        }

        public NotificationLogBuilder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public NotificationLogBuilder read(boolean read) {
            this.read = read;
            return this;
        }

        public NotificationLogBuilder statusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
            return this;
        }

        public NotificationLog build() {
            return new NotificationLog(id, userId, type, recipient, subject, content, status, sentAt, deliveredAt, errorMessage, read, statusMessage);
        }
    }
}

