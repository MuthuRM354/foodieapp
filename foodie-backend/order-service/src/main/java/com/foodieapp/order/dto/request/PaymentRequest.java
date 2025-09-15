package com.foodieapp.order.dto.request;

import java.math.BigDecimal;

/**
 * Data Transfer Object for creating payment requests to the payment service
 */
public class PaymentRequest {
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;

    // Private constructor for builder
    private PaymentRequest() {
    }

    // Getters
    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    // Static method to create a builder
    public static Builder builder() {
        return new Builder();
    }

    // Builder class
    public static class Builder {
        private final PaymentRequest request = new PaymentRequest();

        public Builder orderId(String orderId) {
            request.orderId = orderId;
            return this;
        }

        public Builder userId(String userId) {
            request.userId = userId;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            request.amount = amount;
            return this;
        }

        public Builder currency(String currency) {
            request.currency = currency;
            return this;
        }

        public Builder paymentMethod(String paymentMethod) {
            request.paymentMethod = paymentMethod;
            return this;
        }

        public PaymentRequest build() {
            return request;
        }
    }
}
