package com.foodieapp.notification.dto;

public class EmailRequest extends BaseEmailDTO {
    private String email;
    private String body;
    private String token;
    private String otp;
    private String userId;
    private String template;

    // Only include specific getters and setters not provided by the parent class
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Override to handle fallback to email
    @Override
    public String getTo() {
        return to != null ? to : email;
    }

    public String getBody() {
        return body != null ? body : message;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}