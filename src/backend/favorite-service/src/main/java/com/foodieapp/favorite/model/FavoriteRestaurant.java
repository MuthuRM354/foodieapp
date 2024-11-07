package com.foodieapp.favorite.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "favorite_restaurants")
public class FavoriteRestaurant {
    @Id
    private String id;
    private String userId;
    private String restaurantId;
    private String restaurantName;

    public FavoriteRestaurant(String userId, String restaurantId, String restaurantName) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
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
}