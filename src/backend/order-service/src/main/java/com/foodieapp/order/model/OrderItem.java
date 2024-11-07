package com.foodieapp.order.model;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private String itemId;
    private String itemName;
    private Integer quantity;
    private Double price;
    private Double subtotal;
    private String specialInstructions;
    private Boolean isCustomized;
    private String customizationDetails;

    // Constructors
    public OrderItem() {}

    public OrderItem(String itemId, String itemName, Integer quantity, Double price) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = price * quantity;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        this.calculateSubtotal();
    }

    public Double getPrice() { return price; }
    public void setPrice(Double price) {
        this.price = price;
        this.calculateSubtotal();
    }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public Boolean getIsCustomized() { return isCustomized; }
    public void setIsCustomized(Boolean customized) { isCustomized = customized; }

    public String getCustomizationDetails() { return customizationDetails; }
    public void setCustomizationDetails(String customizationDetails) { this.customizationDetails = customizationDetails; }

    // Helper method
    private void calculateSubtotal() {
        if (this.price != null && this.quantity != null) {
            this.subtotal = this.price * this.quantity;
        }
    }
}
