package com.foodieapp.notification.dto;


import java.util.List;


public class EmailNotificationDTO extends BaseEmailDTO {
    private String body;

    public EmailNotificationDTO() {
    }

    public EmailNotificationDTO(String to, List<String> cc, List<String> bcc, String subject, String message) {
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.message = message;
    }

    public String getBody() {
        return body != null ? body : message;
    }

    public void setBody(String body) {
        this.body = body;
    }
}