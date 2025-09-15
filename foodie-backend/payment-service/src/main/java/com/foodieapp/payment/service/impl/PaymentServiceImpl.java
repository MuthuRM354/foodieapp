package com.foodieapp.payment.service.impl;

import com.foodieapp.payment.client.NotificationServiceClient;
import com.foodieapp.payment.client.OrderServiceClient;
import com.foodieapp.payment.client.UserServiceClient;
import com.foodieapp.payment.dto.OrderDTO;
import com.foodieapp.payment.dto.PaymentRequestDTO;
import com.foodieapp.payment.dto.PaymentResponseDTO;
import com.foodieapp.payment.model.Payment;
import com.foodieapp.payment.exception.PaymentException;
import com.foodieapp.payment.exception.ResourceNotFoundException;
import com.foodieapp.payment.exception.UnauthorizedException;
import com.foodieapp.payment.repository.PaymentRepository;
import com.foodieapp.payment.service.IdGeneratorService;
import com.foodieapp.payment.service.PaymentService;
import com.foodieapp.payment.util.PaymentStatusEnum;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final UserServiceClient userServiceClient;
    private final NotificationServiceClient notificationServiceClient;
    private final OrderServiceClient orderServiceClient;
    private final RazorpayClient razorpayClient;
    private final IdGeneratorService idGeneratorService;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Autowired
    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            UserServiceClient userServiceClient,
            NotificationServiceClient notificationServiceClient,
            OrderServiceClient orderServiceClient,
            RazorpayClient razorpayClient,
            IdGeneratorService idGeneratorService) {
        this.paymentRepository = paymentRepository;
        this.userServiceClient = userServiceClient;
        this.notificationServiceClient = notificationServiceClient;
        this.orderServiceClient = orderServiceClient;
        this.razorpayClient = razorpayClient;
        this.idGeneratorService = idGeneratorService;
    }

    @Override
    @Transactional
    public PaymentResponseDTO createPayment(PaymentRequestDTO paymentRequest, String authToken) {
        logger.info("Creating payment for order: {}, amount: {}", paymentRequest.getOrderId(), paymentRequest.getAmount());

        // Verify user identity from token
        String userId = extractUserIdFromToken(authToken);
        if (userId == null || !userId.equals(paymentRequest.getUserId())) {
            logger.warn("Authorization failed: Token user ID doesn't match request user ID");
            throw new UnauthorizedException("You are not authorized to make this payment");
        }

        // Validate order exists and amount matches
        validateOrderAmount(paymentRequest.getOrderId(), paymentRequest.getAmount());

        // Create payment entity
        Payment payment = new Payment();
        payment.setId(idGeneratorService.generatePaymentId());
        payment.setUserId(paymentRequest.getUserId());
        payment.setOrderId(paymentRequest.getOrderId());
        payment.setAmount(paymentRequest.getAmount());
        payment.setCurrency(paymentRequest.getCurrency());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setStatus(PaymentStatusEnum.PENDING.name());

        // Set timestamps
        LocalDateTime now = LocalDateTime.now();
        payment.setCreatedAt(now);
        payment.setLastUpdated(now);

        // Save initial payment
        Payment savedPayment = paymentRepository.save(payment);
        logger.info("Payment record created with ID: {}", savedPayment.getId());

        // Build response
        PaymentResponseDTO response = convertToDTO(savedPayment);

        // For online payments, we generate a Razorpay order
        if (isOnlinePaymentMethod(paymentRequest.getPaymentMethod())) {
            try {
                // Create order in Razorpay
                JSONObject orderRequest = new JSONObject();
                orderRequest.put("amount", paymentRequest.getAmount().multiply(new BigDecimal("100")).intValue()); // Convert to paise
                orderRequest.put("currency", paymentRequest.getCurrency());
                orderRequest.put("receipt", "receipt_" + savedPayment.getId());

                Order order = razorpayClient.orders.create(orderRequest);

                // Update payment with gateway order ID
                savedPayment.setGatewayOrderId(order.get("id").toString());
                savedPayment = paymentRepository.save(savedPayment);

                // Set Razorpay specific fields in response
                response.setRazorpayOrderId(order.get("id"));
                response.setRazorpayKey(razorpayKeyId);

                logger.info("Razorpay order created: {}", String.valueOf(order.get("id")));            } catch (RazorpayException e) {
                logger.error("Razorpay error: {}", e.getMessage(), e);
                throw new PaymentException("Failed to initiate payment: " + e.getMessage(), e);
            }
        }

        return response;
    }

    @Override
    public PaymentResponseDTO getPaymentById(String paymentId) {
        logger.info("Getting payment with ID: {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    logger.warn("Payment not found with ID: {}", paymentId);
                    return new ResourceNotFoundException("Payment", paymentId);
                });
        return convertToDTO(payment);
    }


    @Override
    public List<PaymentResponseDTO> getPaymentsByOrderId(String orderId) {
        logger.info("Getting payments for order: {}", orderId);

        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        List<PaymentResponseDTO> paymentDTOs = new ArrayList<>();

        for (Payment payment : payments) {
            paymentDTOs.add(convertToDTO(payment));
        }

        logger.debug("Found {} payments for order {}", payments.size(), orderId);
        return paymentDTOs;
    }

    @Override
    @Transactional
    public PaymentResponseDTO updatePaymentStatus(String paymentId, String status) {
        logger.info("Updating payment status: {} -> {}", paymentId, status);

        // Validate payment exists
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    logger.warn("Payment not found with ID: {}", paymentId);
                    return new ResourceNotFoundException("Payment", paymentId);
                });

        // Validate status transition
        validateStatusTransition(payment.getStatus(), status);

        // Update status
        payment.setStatus(status);
        payment.setLastUpdated(LocalDateTime.now());

        // For failed payments, set failure reason
        if (PaymentStatusEnum.FAILED.name().equals(status)) {
            payment.setFailureReason("Payment failed during processing");
        }

        Payment updatedPayment = paymentRepository.save(payment);
        logger.info("Payment status updated successfully: {} -> {}", paymentId, status);

        // Update order payment status
        updateOrderPaymentStatus(updatedPayment);

        // Send notifications based on payment status
        sendPaymentStatusNotifications(updatedPayment);

        return convertToDTO(updatedPayment);
    }

    @Override
    @Transactional
    public void processPaymentCallback(String paymentId, String status, String gatewayTransactionId) {
        logger.info("Processing payment callback: {}, status: {}", paymentId, status);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    logger.warn("Payment not found with ID: {}", paymentId);
                    return new ResourceNotFoundException("Payment", paymentId);
                });

        // Map external status to internal status
        String internalStatus = mapExternalStatusToInternal(status);

        // Validate status transition
        validateStatusTransition(payment.getStatus(), internalStatus);

        // Update payment details
        payment.setStatus(internalStatus);
        payment.setGatewayTransactionId(gatewayTransactionId);
        payment.setLastUpdated(LocalDateTime.now());

        Payment updatedPayment = paymentRepository.save(payment);
        logger.info("Payment status updated from callback: {} -> {}", paymentId, internalStatus);

        // Update order payment status
        updateOrderPaymentStatus(updatedPayment);

        // Send notifications
        sendPaymentStatusNotifications(updatedPayment);

        logger.info("Payment callback processed successfully");
    }

    // Helper methods
    private String extractUserIdFromToken(String authToken) {
        if (authToken == null || authToken.isEmpty()) {
            return null;
        }

        // Extract token from "Bearer" prefix if present
        String token = authToken;
        if (authToken.startsWith("Bearer ")) {
            token = authToken.substring(7);
        }

        // Validate token with user service
        Map<String, Object> validation = userServiceClient.validateToken(token);
        if (validation != null && Boolean.TRUE.equals(validation.get("valid"))) {
            return validation.get("userId").toString();
        }
        return null;
    }

    private void validateOrderAmount(String orderId, BigDecimal requestAmount) {
        try {
            OrderDTO order = orderServiceClient.getOrder(orderId);

            if (order == null) {
                logger.warn("Order not found with ID: {}", orderId);
                throw new ResourceNotFoundException("Order", orderId);
            }

            // Compare amounts with a small tolerance for floating point issues
            if (order.getTotalAmount().subtract(requestAmount).abs().compareTo(new BigDecimal("0.01")) > 0) {
                logger.warn("Payment amount mismatch: Expected {}, Received {}",
                    order.getTotalAmount(), requestAmount);
                throw new PaymentException("Payment amount does not match order amount");
            }
        } catch (Exception e) {
            logger.error("Error validating order amount: {}", e.getMessage(), e);
            throw new PaymentException("Error validating order: " + e.getMessage());
        }
    }

    private void validateStatusTransition(String currentStatus, String newStatus) {
        // Get enum values for type safety
        PaymentStatusEnum current;
        PaymentStatusEnum target;

        try {
            current = PaymentStatusEnum.valueOf(currentStatus);
            target = PaymentStatusEnum.valueOf(newStatus);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid payment status: {}", newStatus);
            throw new PaymentException("Invalid payment status: " + newStatus);
        }

        // Define valid transitions
        boolean isValidTransition = false;

        switch (current) {
            case PENDING:
                // From PENDING can go to any other status
                isValidTransition = true;
                break;
            case PENDING_CONFIRMATION:
                // From PENDING_CONFIRMATION can go to COMPLETED, FAILED or CANCELLED
                isValidTransition = target == PaymentStatusEnum.COMPLETED ||
                                   target == PaymentStatusEnum.FAILED ||
                                   target == PaymentStatusEnum.CANCELLED;
                break;
            case COMPLETED:
                // From COMPLETED can only go to REFUNDED
                isValidTransition = target == PaymentStatusEnum.REFUNDED;
                break;
            case FAILED:
                // Failed is terminal - can't transition
                isValidTransition = false;
                break;
            case REFUNDED:
                // Refunded is terminal - can't transition
                isValidTransition = false;
                break;
            case CANCELLED:
                // Cancelled is terminal - can't transition
                isValidTransition = false;
                break;
        }

        if (!isValidTransition) {
            logger.warn("Invalid payment status transition: {} -> {}", currentStatus, newStatus);
            throw new PaymentException("Invalid payment status transition: " +
                currentStatus + " -> " + newStatus);
        }
    }

    private void updateOrderPaymentStatus(Payment payment) {
        try {
            String orderPaymentStatus;

            switch (PaymentStatusEnum.valueOf(payment.getStatus())) {
                case COMPLETED:
                    orderPaymentStatus = "PAID";
                    break;
                case FAILED:
                    orderPaymentStatus = "PAYMENT_FAILED";
                    break;
                case REFUNDED:
                    orderPaymentStatus = "REFUNDED";
                    break;
                case CANCELLED:
                    orderPaymentStatus = "PAYMENT_CANCELLED";
                    break;
                default:
                    orderPaymentStatus = "PAYMENT_PENDING";
                    break;
            }

            orderServiceClient.updateOrderPaymentStatus(payment.getOrderId(), orderPaymentStatus);
            logger.info("Updated order {} payment status to: {}", payment.getOrderId(), orderPaymentStatus);
        } catch (Exception e) {
            logger.error("Failed to update order payment status: {}", e.getMessage(), e);
            // Don't throw exception here - we don't want to fail the payment processing
            // just because order update failed
        }
    }

    private boolean isOnlinePaymentMethod(String paymentMethod) {
        return "CREDIT_CARD".equals(paymentMethod) ||
                "DEBIT_CARD".equals(paymentMethod) ||
                "UPI".equals(paymentMethod) ||
                "WALLET".equals(paymentMethod) ||
                "RAZORPAY".equals(paymentMethod);
    }

    private String mapExternalStatusToInternal(String externalStatus) {
        if (externalStatus == null) {
            return PaymentStatusEnum.PENDING.name();
        }

        switch (externalStatus.toLowerCase()) {
            case "authorized":
            case "captured":
                return PaymentStatusEnum.COMPLETED.name();
            case "failed":
                return PaymentStatusEnum.FAILED.name();
            case "refunded":
                return PaymentStatusEnum.REFUNDED.name();
            default:
                return PaymentStatusEnum.PENDING.name();
        }
    }

    private PaymentResponseDTO convertToDTO(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setUserId(payment.getUserId());
        dto.setOrderId(payment.getOrderId());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setStatus(payment.getStatus());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setCreatedAt(payment.getCreatedAt());

        // Set failure reason if available
        if (payment.getFailureReason() != null) {
            dto.setMessage(payment.getFailureReason());
        }

        // Set gateway IDs if available
        if (payment.getGatewayOrderId() != null) {
            dto.setRazorpayOrderId(payment.getGatewayOrderId());
        }

        return dto;
    }

    private void sendPaymentStatusNotifications(Payment payment) {
        try {
            // Get user details for notification
            Map<String, Object> userDetails = userServiceClient.getUserById(payment.getUserId(), null);

            if (userDetails != null && userDetails.containsKey("email")) {
                String email = (String) userDetails.get("email");
                String formattedAmount = payment.getAmount() + " " + payment.getCurrency();

                // Send email notification based on status
                if (PaymentStatusEnum.COMPLETED.name().equals(payment.getStatus())) {
                    notificationServiceClient.sendPaymentSuccessEmail(
                            email,
                            payment.getOrderId(),
                            formattedAmount,
                            payment.getGatewayTransactionId());
                } else if (PaymentStatusEnum.FAILED.name().equals(payment.getStatus())) {
                    notificationServiceClient.sendPaymentFailureEmail(
                            email,
                            payment.getOrderId(),
                            formattedAmount,
                            payment.getFailureReason());
                } else if (PaymentStatusEnum.REFUNDED.name().equals(payment.getStatus())) {
                    notificationServiceClient.sendRefundNotification(
                            email,
                            payment.getOrderId(),
                            formattedAmount);
                }

                // Send SMS if phone number is available
                if (userDetails.containsKey("phoneNumber")) {
                    String phoneNumber = (String) userDetails.get("phoneNumber");
                    boolean successful = PaymentStatusEnum.COMPLETED.name().equals(payment.getStatus());

                    notificationServiceClient.sendPaymentStatusSms(
                            phoneNumber,
                            payment.getOrderId(),
                            successful,
                            formattedAmount);
                }
            } else {
                logger.warn("User details not found or email missing for user: {}", payment.getUserId());
            }
        } catch (Exception e) {
            logger.error("Failed to send payment notification: {}", e.getMessage(), e);
            // Don't throw exception here - notifications should not affect core flow
        }
    }
}
