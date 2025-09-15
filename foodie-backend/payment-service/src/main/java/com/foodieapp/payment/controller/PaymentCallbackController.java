package com.foodieapp.payment.controller;

import com.foodieapp.payment.dto.RazorpayVerificationDTO;
import com.foodieapp.payment.exception.PaymentException;
import com.foodieapp.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments/callback")
public class PaymentCallbackController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentCallbackController.class);
    private final PaymentService paymentService;

    @Autowired
    public PaymentCallbackController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/razorpay")
    public ResponseEntity<String> razorpayCallback(@RequestBody RazorpayVerificationDTO verificationData) {
        logger.info("Received Razorpay callback for payment: {}", verificationData.getPaymentId());

        // Validate callback data
        if (verificationData.getPaymentId() == null || verificationData.getPaymentId().trim().isEmpty()) {
            logger.error("Invalid callback: Missing payment ID");
            throw new PaymentException("Invalid callback: Missing payment ID");
        }

        if (verificationData.getStatus() == null || verificationData.getStatus().trim().isEmpty()) {
            logger.error("Invalid callback: Missing payment status");
            throw new PaymentException("Invalid callback: Missing payment status");
        }

        // Extract payment ID from Razorpay's payment ID
        String paymentId = verificationData.getPaymentId();
        String status = verificationData.getStatus();
        String gatewayTransactionId = verificationData.getRazorpayPaymentId();

        // Process the payment callback
        paymentService.processPaymentCallback(paymentId, status, gatewayTransactionId);
        logger.info("Successfully processed callback for payment: {}", paymentId);

        return ResponseEntity.ok("Callback processed successfully");
    }
}