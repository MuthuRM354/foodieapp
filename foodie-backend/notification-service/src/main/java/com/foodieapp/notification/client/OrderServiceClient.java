package com.foodieapp.notification.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class OrderServiceClient extends AbstractServiceClient {
    // Removed duplicate logger - using the one from AbstractServiceClient

    public OrderServiceClient(
            RestTemplate restTemplate,
            @Value("${order.service.url}") String orderServiceUrl) {
        super(restTemplate, orderServiceUrl);
    }

    public Map<String, Object> getOrderDetails(String orderId) {
        // Simplified to use parent class method directly without redundant try-catch
        return getForObject("/api/v1/orders/" + orderId, Map.class);
    }

    public Map<String, Object> getUserOrders(String userId, String token) {
        // Removed redundant try-catch block
        return exchangeWithAuth(
                "/api/v1/orders/user/" + userId,
                HttpMethod.GET,
                null,
                Map.class,
                token).getBody();
    }
}