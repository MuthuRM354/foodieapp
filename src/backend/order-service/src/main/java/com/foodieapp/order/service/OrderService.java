package com.foodieapp.order.service;

import com.foodieapp.order.model.Order;
import com.foodieapp.order.model.OrderItem;
import com.foodieapp.order.model.OrderStatus;
import com.foodieapp.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(Order order) {
        order.setOrderTime(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        calculateOrderTotal(order);
        return orderRepository.save(order);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getOrdersByUser(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getOrdersByRestaurant(Long restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public Order updateOrder(Long orderId, Order orderDetails) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setItems(orderDetails.getItems());
        order.setDeliveryAddress(orderDetails.getDeliveryAddress());
        order.setSpecialInstructions(orderDetails.getSpecialInstructions());
        calculateOrderTotal(order);

        return orderRepository.save(order);
    }

    public List<Order> getOrdersByDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByUserIdAndOrderTimeBetween(userId, startDate, endDate);
    }

    public List<Order> getRestaurantOrdersByDateRange(Long restaurantId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByRestaurantIdAndOrderTimeBetween(restaurantId, startDate, endDate);
    }

    private void calculateOrderTotal(Order order) {
        double total = order.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        order.setTotalAmount(total);
    }
}
