package com.foodieapp.favorite.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;

@Document(collection = "favorite_cuisines")
public class FavoriteCuisine {
    @Id
    private String id;
    private String userId;
    private String cuisineId;
    private LocalDateTime createdAt;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCuisineId() { return cuisineId; }
    public void setCuisineId(String cuisineId) { this.cuisineId = cuisineId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
