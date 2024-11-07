package com.foodieapp.restaurant.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "restaurants")
public class Restaurant {
    @Id
    private String id;
    private String name;
    private String ownerId;
    private String location;
    private String cuisine;
    private List<MenuItem> menu;
    private String status; // ACTIVE, PENDING, INACTIVE
    private double rating;
    private String imageUrl;
    private String description;

    // Constructor
    public Restaurant(String name, String ownerId, String location, String cuisine) {
        this.name = name;
        this.ownerId = ownerId;
        this.location = location;
        this.cuisine = cuisine;
        this.status = "PENDING";
        this.rating = 0.0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }
    public List<MenuItem> getMenu() { return menu; }
    public void setMenu(List<MenuItem> menu) { this.menu = menu; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}