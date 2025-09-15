package com.foodieapp.order.model;

import com.foodieapp.order.util.PriceCalculationService;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "carts")
public class Cart {
    @Id
    private String id;
    private String userId;
    private String restaurantId;
    private List<CartItem> items = new ArrayList<>();
    private BigDecimal totalAmount;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal deliveryFee;
    private BigDecimal discount;
    private LocalDateTime lastUpdated;
    private String specialInstructions;
    private boolean isActive;
    private String promoCode;
    private int itemCount;

    // This will be injected by the service layer
    private transient PriceCalculationService priceCalculationService;

    public Cart() {
        this.totalAmount = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
        this.tax = BigDecimal.ZERO;
        this.deliveryFee = BigDecimal.ZERO;
        this.discount = BigDecimal.ZERO;
        this.isActive = true;
        this.lastUpdated = LocalDateTime.now();
    }

    public void setPriceCalculationService(PriceCalculationService priceCalculationService) {
        this.priceCalculationService = priceCalculationService;
    }

    public void calculateTotal() {
        if (priceCalculationService != null) {
            PriceCalculationService.PriceBreakdown breakdown = priceCalculationService.calculateCartTotals(items);
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
                this.subtotal, this.tax, this.deliveryFee, this.discount);
        }

        this.itemCount = items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }

    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(BigDecimal deliveryFee) { this.deliveryFee = deliveryFee; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
}
