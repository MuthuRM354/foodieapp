package com.foodieapp.restaurant.dto.response;

import java.util.List;
import java.util.Map;

public class RestaurantDetailResponse {
    private String id;
    private String name;
    private String description;
    private String cuisine;
    private Double rating;
    private String imageUrl;
    private String address;
    private String phoneNumber;
    private String email;
    private String openingHours;
    private String closingHours;
    private Map<String, List<MenuItemResponse>> menuByCategory;
    private boolean isOpen;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }

    public String getClosingHours() { return closingHours; }
    public void setClosingHours(String closingHours) { this.closingHours = closingHours; }

    public Map<String, List<MenuItemResponse>> getMenuByCategory() { return menuByCategory; }
    public void setMenuByCategory(Map<String, List<MenuItemResponse>> menuByCategory) { this.menuByCategory = menuByCategory; }

    public boolean isOpen() { return isOpen; }
    public void setOpen(boolean open) { isOpen = open; }
}
