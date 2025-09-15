package com.foodieapp.order.dto.request;

import com.foodieapp.order.validation.ValidationConstants;

public class CartItemRequest extends BaseItemRequest {
    @ValidationConstants.RestaurantId
    private String restaurantId;

    // Restaurant-specific getters and setters
    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }
}
