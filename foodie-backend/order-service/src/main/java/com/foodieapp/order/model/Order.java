package com.foodieapp.order.model;

import com.foodieapp.order.util.PriceCalculationService;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String userId;
    private String restaurantId;
    private String restaurantName;
    private List<OrderItem> items = new ArrayList<>();
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal deliveryFee;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String deliveryAddress;
    private String paymentMethod;
    private LocalDateTime orderTime;
    private LocalDateTime confirmedTime;
    private String specialInstructions;
    private String customerName;
    private String customerPhone;
    private String orderNotes;

    // This will be injected by the service layer
    private transient PriceCalculationService priceCalculationService;

    public Order() {
        this.items = new ArrayList<>();
        this.orderTime = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
        this.subtotal = BigDecimal.ZERO;
        this.tax = BigDecimal.ZERO;
        this.deliveryFee = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
    }

    public void setPriceCalculationService(PriceCalculationService priceCalculationService) {
        this.priceCalculationService = priceCalculationService;
    }

    public void calculateTotals() {
        if (priceCalculationService != null) {
            PriceCalculationService.PriceBreakdown breakdown = priceCalculationService.calculateOrderTotals(items);
            this.subtotal = breakdown.getSubtotal();
            this.tax = breakdown.getTax();
            this.deliveryFee = breakdown.getDeliveryFee();
            this.totalAmount = breakdown.getTotalAmount();
        } else {
            // Fallback to the original calculation for backward compatibility
            this.subtotal = items.stream()
                    .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Use the static utility methods
            this.tax = com.foodieapp.order.util.PriceCalculationUtil.calculateTax(this.subtotal);
            this.deliveryFee = com.foodieapp.order.util.PriceCalculationUtil.getDeliveryFee();
            this.totalAmount = com.foodieapp.order.util.PriceCalculationUtil.calculateTotal(
                this.subtotal, this.tax, this.deliveryFee, null);
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) {
        this.items = items;
        calculateTotals();
    }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }

    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(BigDecimal deliveryFee) { this.deliveryFee = deliveryFee; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public LocalDateTime getOrderTime() { return orderTime; }
    public void setOrderTime(LocalDateTime orderTime) { this.orderTime = orderTime; }

    public LocalDateTime getConfirmedTime() { return confirmedTime; }
    public void setConfirmedTime(LocalDateTime confirmedTime) { this.confirmedTime = confirmedTime; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getOrderNotes() { return orderNotes; }
    public void setOrderNotes(String orderNotes) { this.orderNotes = orderNotes; }
}
