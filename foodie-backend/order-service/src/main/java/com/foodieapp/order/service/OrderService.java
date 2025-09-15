package com.foodieapp.order.service;

import com.foodieapp.order.dto.request.OrderRequest;
import com.foodieapp.order.dto.response.OrderResponse;
import com.foodieapp.order.model.Order;
import com.foodieapp.order.model.OrderStatus;

import java.util.List;

public interface OrderService {
    // Order creation methods
    Order createOrderFromCart(OrderRequest orderRequest, String authToken);
    Order createDirectOrder(OrderRequest orderRequest, String authToken);

    // Order retrieval methods
    Order getOrderById(String orderId, String authToken);
    List<Order> getUserOrders(String userId, String authToken);
    List<Order> getRestaurantOrders(String restaurantId, String authToken);
    List<Order> getAllOrders(int page, int size, String authToken);

    // Order status management
    Order updateOrderStatus(String orderId, OrderStatus status, String notes, String authToken);

    // Conversion method
    OrderResponse convertToOrderResponse(Order order);

    /**
     * Update order payment status
     * @param orderId The order ID
     * @param paymentStatus The payment status
     * @return Updated order
     */
    Order updateOrderPaymentStatus(String orderId, String paymentStatus);
}
