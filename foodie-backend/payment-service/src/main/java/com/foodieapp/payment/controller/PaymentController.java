package com.foodieapp.payment.controller;

import com.foodieapp.payment.client.UserServiceClient;
import com.foodieapp.payment.dto.PaymentRequestDTO;
import com.foodieapp.payment.dto.PaymentResponseDTO;
import com.foodieapp.payment.exception.UnauthorizedException;
import com.foodieapp.payment.service.PaymentService;
import org.springframework.validation.annotation.Validated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final UserServiceClient userServiceClient;

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    public PaymentController(PaymentService paymentService, UserServiceClient userServiceClient) {
        this.paymentService = paymentService;
        this.userServiceClient = userServiceClient;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(
            @Validated @RequestBody PaymentRequestDTO paymentRequest,
            @RequestHeader("Authorization") String authHeader) {

        logger.info("Creating payment for order: {}", paymentRequest.getOrderId());

        // Validate request data
        if (paymentRequest.getOrderId() == null || paymentRequest.getOrderId().trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be empty");
        }

        if (paymentRequest.getUserId() == null || paymentRequest.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }

        if (paymentRequest.getAmount() == null || paymentRequest.getAmount().doubleValue() <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }

        PaymentResponseDTO response = paymentService.createPayment(paymentRequest, authHeader);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDTO> getPayment(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String paymentId) {

        if (paymentId == null || paymentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment ID cannot be empty");
        }

        PaymentResponseDTO payment = paymentService.getPaymentById(paymentId);

        // Only allow users to access their own payments unless admin
        validateUserAccess(authHeader, payment.getUserId());

        return ResponseEntity.ok(payment);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByOrder(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String orderId) {

        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be empty");
        }

        List<PaymentResponseDTO> payments = paymentService.getPaymentsByOrderId(orderId);

        if (!payments.isEmpty()) {
            // Verify the user has permission to access these payments
            validateUserAccess(authHeader, payments.get(0).getUserId());
        }

        return ResponseEntity.ok(payments);
    }

    @PutMapping("/{paymentId}/status")
    public ResponseEntity<PaymentResponseDTO> updatePaymentStatus(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String paymentId,
            @RequestBody String status) {

        if (paymentId == null || paymentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment ID cannot be empty");
        }

        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }

        // Get payment to check ownership
        PaymentResponseDTO payment = paymentService.getPaymentById(paymentId);

        // Only allow users to update their own payments unless admin
        validateUserAccess(authHeader, payment.getUserId());

        PaymentResponseDTO response = paymentService.updatePaymentStatus(paymentId, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Validates that the user making the request has access to the specified user's data
     * Either they are that user, or they are an admin
     */
    private void validateUserAccess(String authHeader, String userId) {
        if (authHeader == null || authHeader.isEmpty()) {
            logger.warn("Authentication required for accessing payment data");
            throw new UnauthorizedException("Authentication required");
        }

        String token = extractToken(authHeader);

        // Validate token with user service
        Map<String, Object> validation = userServiceClient.validateToken(token);

        if (validation == null || !Boolean.TRUE.equals(validation.get("valid"))) {
            logger.warn("Invalid authentication token provided");
            throw new UnauthorizedException("Invalid authentication token");
        }

        String tokenUserId = validation.get("userId") != null ? validation.get("userId").toString() : null;
        boolean isAdmin = validation.get("role") != null && "ADMIN".equals(validation.get("role").toString());

        // Check if user is accessing their own data or is an admin
        if (!isAdmin && (tokenUserId == null || !tokenUserId.equals(userId))) {
            logger.warn("User {} attempted to access data for user {}", tokenUserId, userId);
            throw new UnauthorizedException("You don't have permission to access this payment");
        }
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }
}