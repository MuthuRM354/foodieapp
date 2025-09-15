package com.foodieapp.order.client;

import com.foodieapp.order.dto.request.PaymentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PaymentServiceClient extends BaseServiceClient {

    public PaymentServiceClient(
            RestTemplate restTemplate,
            @Value("${payment.service.url}") String paymentServiceUrl) {
        super(restTemplate, paymentServiceUrl);
    }

    /**
     * Initiate a payment for an order
     */
    public Map<String, Object> createPayment(PaymentRequest paymentRequest, String token) {
        try {
            String path = "/api/payments";

            // Convert PaymentRequest to Map for the API call
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("orderId", paymentRequest.getOrderId());
            requestBody.put("userId", paymentRequest.getUserId());
            requestBody.put("amount", paymentRequest.getAmount());
            requestBody.put("currency", paymentRequest.getCurrency());
            requestBody.put("paymentMethod", paymentRequest.getPaymentMethod());

            Map<String, Object> response = postForObject(path, requestBody, Map.class, token);

            if (response != null) {
                return response;
            } else {
                logger.error("Received null response when creating payment");
                return Collections.singletonMap("error", "Failed to process payment");
            }
        } catch (Exception e) {
            return handleApiCallException("creating payment", e,
                    Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * Convenience method to create payment with individual parameters
     * @deprecated Use {@link #createPayment(PaymentRequest, String)} instead
     */
    @Deprecated
    public Map<String, Object> createPayment(String userId, String orderId, BigDecimal amount,
                                             String currency, String paymentMethod, String token) {
        PaymentRequest request = PaymentRequest.builder()
                .userId(userId)
                .orderId(orderId)
                .amount(amount)
                .currency(currency)
                .paymentMethod(paymentMethod)
                .build();

        return createPayment(request, token);
    }

    /**
     * Get payment information by ID
     */
    public Map<String, Object> getPayment(String paymentId, String token) {
        String cacheKey = "payment_" + paymentId;

        return getCachedOrCompute(cacheKey, () -> {
            try {
                String path = "/api/payments/" + paymentId;
                Map<String, Object> response = getForObject(path, Map.class, token);

                if (response != null) {
                    return response;
                } else {
                    logger.error("Received null response when getting payment details");
                    return Collections.singletonMap("error", "Failed to get payment information");
                }
            } catch (Exception e) {
                return handleApiCallException("getting payment", e,
                        Collections.singletonMap("error", e.getMessage()));
            }
        });
    }

    /**
     * Get payments for an order
     */
    public List<Map<String, Object>> getPaymentsByOrder(String orderId, String token) {
        String cacheKey = "payments_for_order_" + orderId;

        return getCachedOrCompute(cacheKey, () -> {
            try {
                String path = "/api/payments/order/" + orderId;
                List<Map<String, Object>> response = getForObject(path, List.class, token);

                if (response != null) {
                    return response;
                } else {
                    logger.error("Received null response when getting payments for order");
                    return Collections.emptyList();
                }
            } catch (Exception e) {
                return handleApiCallException("getting payments for order", e, Collections.emptyList());
            }
        });
    }

    /**
     * Update payment status
     */
    public Map<String, Object> updatePaymentStatus(String paymentId, String status, String token) {
        try {
            String path = "/api/payments/" + paymentId + "/status";

            // Clear cache for this payment
            clearCache("payment_" + paymentId);

            Map<String, Object> response = putForObject(path, status, Map.class, token);

            if (response != null) {
                return response;
            } else {
                logger.error("Received null response when updating payment status");
                return Collections.singletonMap("error", "Failed to update payment status");
            }
        } catch (Exception e) {
            return handleApiCallException("updating payment status", e,
                    Collections.singletonMap("error", e.getMessage()));
        }
    }
}
