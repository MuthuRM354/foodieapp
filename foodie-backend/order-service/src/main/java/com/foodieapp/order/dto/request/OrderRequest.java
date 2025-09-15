package com.foodieapp.order.dto.request;

import com.foodieapp.order.validation.ValidationConstants;
import com.foodieapp.order.validation.ValidationGroups;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;

import java.util.List;

public class OrderRequest {
    public enum OrderSource {
        CART,
        DIRECT
    }

    @NotNull(message = "Order source is required")
    private OrderSource source;

    @ValidationConstants.UserId
    private String userId;

    // Required for direct orders, optional for cart orders
    private String restaurantId;
    private String restaurantName;

    // Required for direct orders, not used for cart orders
    @Valid
    private List<OrderItemRequest> items;

    @ValidationConstants.Address
    private String deliveryAddress;

    @ValidationConstants.PaymentMethod
    private String paymentMethod;

    @ValidationConstants.CustomerName
    private String customerName;

    @ValidationConstants.PhoneNumber
    private String customerPhone;

    @ValidationConstants.SpecialInstructions
    private String specialInstructions;

    // Getters and Setters
    public OrderSource getSource() { return source; }
    public void setSource(OrderSource source) { this.source = source; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }
}