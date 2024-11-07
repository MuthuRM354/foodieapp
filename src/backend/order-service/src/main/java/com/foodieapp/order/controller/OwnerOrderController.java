package com.foodieapp.order.controller;

import com.foodieapp.order.model.Order;
import com.foodieapp.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/restaurant/orders")
public class OwnerOrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/{restaurantId}")
    public ResponseEntity<List<Order>> getRestaurantOrders(@PathVariable String restaurantId) {
        return ResponseEntity.ok(orderService.getRestaurantOrders(restaurantId));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }
}
