package com.foodieapp.notification.dto;

import java.util.List;

/**
 * Base class for all email-related DTOs to avoid duplication
 */
public abstract class BaseEmailDTO {
    protected String to;
    protected String subject;
    protected String message;
    protected List<String> cc;
    protected List<String> bcc;
    protected boolean html = true;

    // Common getters and setters
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

    public List<String> getCc() {
        return cc;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public List<String> getBcc() {
        return bcc;
    }

    public void setBcc(List<String> bcc) {
        this.bcc = bcc;
    }

    public boolean isHtml() {
        return html;
    }

    public void setHtml(boolean html) {
        this.html = html;
    }
}