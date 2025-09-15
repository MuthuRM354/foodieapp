package com.foodieapp.restaurant.util.mapper;

import com.foodieapp.restaurant.dto.request.RestaurantRequest;
import com.foodieapp.restaurant.dto.response.MenuItemResponse;
import com.foodieapp.restaurant.dto.response.RestaurantDTO;
import com.foodieapp.restaurant.dto.response.RestaurantDetailResponse;
import com.foodieapp.restaurant.model.MenuItem;
import com.foodieapp.restaurant.model.Restaurant;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Unified mapper for all entity-to-DTO conversions
 */
public class EntityMapper {

    // Restaurant mappers
    public static RestaurantDetailResponse toRestaurantResponse(Restaurant restaurant) {
        RestaurantDetailResponse response = new RestaurantDetailResponse();
        response.setId(restaurant.getId());
        response.setName(restaurant.getName());
        response.setDescription(restaurant.getDescription());
        response.setCuisine(restaurant.getCuisine());
        response.setRating(restaurant.getRating());
        response.setImageUrl(restaurant.getImageUrl());
        response.setAddress(restaurant.getAddress());
        response.setPhoneNumber(restaurant.getPhoneNumber());
        response.setEmail(restaurant.getEmail());
        response.setOpeningHours(restaurant.getOpeningHours());
        response.setClosingHours(restaurant.getClosingHours());
        response.setOpen(isRestaurantOpen(restaurant.getOpeningHours(), restaurant.getClosingHours()));
        response.setMenuByCategory(new HashMap<>());
        return response;
    }

    public static Restaurant toRestaurantEntity(RestaurantRequest request) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setAddress(request.getAddress());
        restaurant.setPhoneNumber(request.getPhoneNumber());
        restaurant.setEmail(request.getEmail());
        restaurant.setCuisine(request.getCuisine());
        restaurant.setOpeningHours(request.getOpeningHours());
        restaurant.setClosingHours(request.getClosingHours());
        restaurant.setOwnerId(request.getOwnerId());
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant.setIsActive(true);
        restaurant.setIsVerified(false);
        restaurant.setImageUrl(request.getImageUrl());
        return restaurant;
    }

    public static RestaurantDTO toRestaurantDTO(Restaurant restaurant) {
        RestaurantDTO dto = new RestaurantDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setDescription(restaurant.getDescription());
        dto.setCuisine(restaurant.getCuisine());
        dto.setRating(restaurant.getRating());
        dto.setImageUrl(restaurant.getImageUrl());
        dto.setAddress(restaurant.getAddress());
        dto.setOpen(isRestaurantOpen(restaurant.getOpeningHours(), restaurant.getClosingHours()));
        return dto;
    }

    // MenuItem mappers
    public static MenuItemResponse toMenuItemResponse(MenuItem menuItem) {
        MenuItemResponse response = new MenuItemResponse();
        response.setId(menuItem.getId());
        response.setName(menuItem.getName());
        response.setDescription(menuItem.getDescription());
        response.setPrice(menuItem.getPrice());
        response.setImageUrl(menuItem.getImageUrl());
        response.setIsVegetarian(menuItem.getIsVegetarian());
        response.setSpicyLevel(menuItem.getSpicyLevel());
        return response;
    }

    public static List<MenuItemResponse> toMenuItemResponseList(List<MenuItem> menuItems) {
        return menuItems.stream()
                .map(EntityMapper::toMenuItemResponse)
                .collect(Collectors.toList());
    }

    // Utility methods
    public static boolean isRestaurantOpen(String openingHours, String closingHours) {
        if (openingHours == null || closingHours == null) {
            return false;
        }

        try {
            LocalTime now = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime opening = LocalTime.parse(openingHours, formatter);
            LocalTime closing = LocalTime.parse(closingHours, formatter);

            return !now.isBefore(opening) && !now.isAfter(closing);
        } catch (Exception e) {
            return false;
        }
    }
}