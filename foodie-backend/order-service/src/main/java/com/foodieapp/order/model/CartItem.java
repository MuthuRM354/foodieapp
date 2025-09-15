package com.foodieapp.order.model;

import com.foodieapp.order.util.PriceCalculationUtil;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartItem {
    private String itemId;
    private String name;
    private Integer quantity;
    private BigDecimal price;
    private String restaurantId;
    private String specialInstructions;
    private String category;
    private String size;
    private List<String> customizations;
    private Map<String, BigDecimal> addOns;
    private boolean available;
    private String imageUrl;
    private BigDecimal subtotal;

    public CartItem() {
        this.quantity = 1;
        this.addOns = new HashMap<>();
        this.available = true;
    }

    public void calculateSubtotal() {
        this.subtotal = PriceCalculationUtil.calculateSubtotalWithAddons(price, quantity, addOns);
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

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public List<String> getCustomizations() { return customizations; }
    public void setCustomizations(List<String> customizations) { this.customizations = customizations; }

    public Map<String, BigDecimal> getAddOns() { return addOns; }
    public void setAddOns(Map<String, BigDecimal> addOns) {
        this.addOns = addOns;
        calculateSubtotal();
    }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public BigDecimal getSubtotal() { return subtotal; }
}
