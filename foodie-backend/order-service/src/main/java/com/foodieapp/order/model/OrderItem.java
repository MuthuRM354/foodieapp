package com.foodieapp.order.model;

import com.foodieapp.order.util.PriceCalculationUtil;
import java.math.BigDecimal;

public class OrderItem {
    private String itemId;
    private String name;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
    private String specialInstructions;
    private String category;
    private String size;

    public OrderItem() {
        this.quantity = 1;
        this.price = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }

    public void calculateSubtotal() {
        this.subtotal = PriceCalculationUtil.calculateSubtotal(price, quantity);
    }

    // Getters and Setters
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateSubtotal();
    }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) {
        this.price = price;
        calculateSubtotal();
    }

    public BigDecimal getSubtotal() { return subtotal; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
}
