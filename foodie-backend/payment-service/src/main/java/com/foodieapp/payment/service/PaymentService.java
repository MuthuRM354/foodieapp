package com.foodieapp.payment.service;

import com.foodieapp.payment.dto.PaymentRequestDTO;
import com.foodieapp.payment.dto.PaymentResponseDTO;
import java.util.List;

public interface PaymentService {
    PaymentResponseDTO createPayment(PaymentRequestDTO paymentRequest, String authToken);
    PaymentResponseDTO getPaymentById(String paymentId);
    List<PaymentResponseDTO> getPaymentsByOrderId(String orderId);
    PaymentResponseDTO updatePaymentStatus(String paymentId, String status);
    void processPaymentCallback(String paymentId, String status, String gatewayTransactionId);
}