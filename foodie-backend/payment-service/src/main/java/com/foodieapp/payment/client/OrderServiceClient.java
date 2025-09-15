package com.foodieapp.payment.client;

import com.foodieapp.payment.dto.OrderDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrderServiceClient extends ServiceClient {

    public OrderServiceClient(RestTemplate restTemplate,
                              @Value("${services.order.url:${order.service.url}}") String baseUrl) {
        super(restTemplate, baseUrl);
    }

    /**
     * Get order details by ID
     */
    public OrderDTO getOrder(String orderId) {
        if (orderId == null) {
            logger.warn("Attempt to get order with null ID");
            return null;
        }

        try {
            logger.debug("Getting order details for ID: {}", orderId);
            ResponseEntity<OrderDTO> response = exchange(
                    "/api/v1/orders/" + orderId,
                    HttpMethod.GET,
                    null,
                    OrderDTO.class
            );
            return response.getBody();
        } catch (Exception e) {
            handleApiCallException("retrieving order details", e, null);
            return null;
        }
    }

    /**
     * Update order payment status
     */
    public void updateOrderPaymentStatus(String orderId, String paymentStatus) {
        if (orderId == null || paymentStatus == null || paymentStatus.isEmpty()) {
            logger.warn("Invalid parameters for updating order payment status");
            return;
        }

        try {
            logger.info("Updating payment status for order {}: {}", orderId, paymentStatus);
            exchange(
                    "/api/v1/orders/" + orderId + "/payment-status",
                    HttpMethod.PUT,
                    paymentStatus,
                    Void.class
            );
            logger.debug("Order payment status updated successfully");
        } catch (Exception e) {
            handleApiCallException("updating order payment status", e, null);
        }
    }
}
