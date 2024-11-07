package com.foodieapp.order.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
public class Order {

    @Id
    private String id;
    private String userId;
    private String restaurantId;
    private List<OrderItem> items;
    private double totalAmount;
    private String status;
    private LocalDateTime orderTime;
    private String deliveryAddress;
    private String paymentStatus;

    // Constructor
    public Order(String userId, String restaurantId, List<OrderItem> items,
                double totalAmount, String deliveryAddress) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = "PENDING";
        this.paymentStatus = "PENDING";
        this.orderTime = LocalDateTime.now();
        this.deliveryAddress = deliveryAddress;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getOrderTime() { return orderTime; }
    public void setOrderTime(LocalDateTime orderTime) { this.orderTime = orderTime; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}