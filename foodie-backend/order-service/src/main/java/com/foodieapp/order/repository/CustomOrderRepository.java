package com.foodieapp.order.repository;

import com.foodieapp.order.model.Order;
import com.foodieapp.order.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomOrderRepository {
    List<Order> findOrdersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Order> findOrdersByStatusForRestaurant(String restaurantId, OrderStatus status);
    List<Order> findUndeliveredOrdersOlderThan(LocalDateTime threshold);
    long countOrdersByUserRestaurant(String userId, String restaurantId);
}