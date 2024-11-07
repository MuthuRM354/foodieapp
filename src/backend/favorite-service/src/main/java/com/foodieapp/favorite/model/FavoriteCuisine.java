package com.foodieapp.favorite.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "favorite_cuisines")
public class FavoriteCuisine {
    @Id
    private String id;
    private String userId;
    private String cuisineId;
    private String cuisineName;

    public FavoriteCuisine(String userId, String cuisineId, String cuisineName) {
        this.userId = userId;
        this.cuisineId = cuisineId;
        this.cuisineName = cuisineName;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getCuisineId() { return cuisineId; }
    public void setCuisineId(String cuisineId) { this.cuisineId = cuisineId; }
    public String getCuisineName() { return cuisineName; }
    public void setCuisineName(String cuisineName) { this.cuisineName = cuisineName; }
}