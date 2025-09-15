package com.foodieapp.order.model;

public enum OrderStatus {
    PENDING,      // Initial state when order is created
    CONFIRMED,    // Restaurant has accepted the order
    PREPARING,    // Restaurant is preparing the food
    READY,        // Food is ready for pickup or delivery
    OUT_FOR_DELIVERY, // Food is on the way
    DELIVERED,    // Food has been delivered
    COMPLETED,    // Order has been completed successfully
    CANCELLED     // Order has been cancelled
}
