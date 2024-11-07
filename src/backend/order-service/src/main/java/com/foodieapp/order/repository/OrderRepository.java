package com.foodieapp.order.repository;

import com.foodieapp.order.model.Order;
import com.foodieapp.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(String userId);
    List<Order> findByRestaurantId(Long restaurantId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByRestaurantIdAndStatus(Long restaurantId, OrderStatus status);
    List<Order> findByUserIdAndOrderTimeBetween(String userId, LocalDateTime startDate, LocalDateTime endDate);
    List<Order> findByRestaurantIdAndOrderTimeBetween(Long restaurantId, LocalDateTime startDate, LocalDateTime endDate);
}
