package com.foodieapp.order.controller;

import com.foodieapp.order.model.Order;
import com.foodieapp.order.model.OrderStatus;
import com.foodieapp.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/restaurant/orders")
public class OwnerOrderController {

    private final OrderService orderService;

    @Autowired
    public OwnerOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Order>> getRestaurantOrders(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(orderService.getOrdersByRestaurant(restaurantId));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    @GetMapping("/restaurant/{restaurantId}/history")
    public ResponseEntity<List<Order>> getRestaurantOrderHistory(
            @PathVariable Long restaurantId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(orderService.getRestaurantOrdersByDateRange(restaurantId, startDate, endDate));
    }
}
