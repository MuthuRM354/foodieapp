package com.foodieapp.order.controller;

import com.foodieapp.order.dto.request.OrderRequest;
import com.foodieapp.order.dto.request.OrderStatusUpdateRequest;
import com.foodieapp.order.dto.response.OrderResponse;
import com.foodieapp.order.model.Order;
import com.foodieapp.order.model.OrderStatus;
import com.foodieapp.order.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    /* Customer Endpoints */

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest orderRequest,
            @RequestHeader("Authorization") String authToken) {

        Order order;

        if (orderRequest.getSource() == OrderRequest.OrderSource.CART) {
            // Forward auth token for user validation
            order = orderService.createOrderFromCart(orderRequest, authToken);
        } else {
            // Forward auth token for user validation
            order = orderService.createDirectOrder(orderRequest, authToken);
        }

        return ResponseEntity.ok(orderService.convertToOrderResponse(order));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetails(
            @PathVariable String orderId,
            @RequestHeader("Authorization") String authToken) {

        // Forward auth token for authorization verification
        Order order = orderService.getOrderById(orderId, authToken);
        return ResponseEntity.ok(orderService.convertToOrderResponse(order));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            @PathVariable String userId,
            @RequestHeader("Authorization") String authToken) {

        // Forward auth token for authorization verification
        List<OrderResponse> responses = orderService.getUserOrders(userId, authToken).stream()
                .map(orderService::convertToOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /* Restaurant Endpoints */

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<OrderResponse>> getRestaurantOrders(
            @PathVariable String restaurantId,
            @RequestHeader("Authorization") String authToken) {

        // Forward auth token for authorization verification
        List<OrderResponse> orders = orderService.getRestaurantOrders(restaurantId, authToken).stream()
                .map(orderService::convertToOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    /* Order Status Management - Unified into single endpoint */

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable String orderId,
            @Valid @RequestBody OrderStatusUpdateRequest request,
            @RequestHeader("Authorization") String authToken) {

        // Forward auth token for authorization verification
        Order updatedOrder = orderService.updateOrderStatus(
                orderId,
                request.getStatus(),
                request.getNotes(),
                authToken);

        return ResponseEntity.ok(orderService.convertToOrderResponse(updatedOrder));
    }

    // Admin endpoints for order management
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String authToken) {

        List<OrderResponse> responses = orderService.getAllOrders(page, size, authToken).stream()
                .map(orderService::convertToOrderResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{orderId}/payment-status")
    public ResponseEntity<OrderResponse> updateOrderPaymentStatus(
            @PathVariable String orderId,
            @RequestBody String paymentStatus) {

        logger.info("Received payment status update for order {}: {}", orderId, paymentStatus);

        // Call orderService to handle the payment status update
        Order updatedOrder = orderService.updateOrderPaymentStatus(orderId, paymentStatus);

        return ResponseEntity.ok(orderService.convertToOrderResponse(updatedOrder));
    }
}
