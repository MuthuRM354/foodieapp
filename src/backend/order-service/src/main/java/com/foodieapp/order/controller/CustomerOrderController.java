package com.foodieapp.order.controller;

import com.foodieapp.order.model.Order;
import com.foodieapp.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/customer/orders")
public class CustomerOrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Order>> getCustomerOrders(@PathVariable String userId) {
        return ResponseEntity.ok(orderService.getCustomerOrders(userId));
    }
}
