package com.foodieapp.restaurant.service;

import com.foodieapp.restaurant.model.Restaurant;
import com.foodieapp.restaurant.model.MenuItem;
import com.foodieapp.restaurant.repository.RestaurantRepository;
import com.foodieapp.restaurant.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository, MenuRepository menuRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuRepository = menuRepository;
    }

    public Restaurant createRestaurant(Restaurant restaurant) {
        restaurant.setIsActive(true);
        return restaurantRepository.save(restaurant);
    }

    public Optional<Restaurant> getRestaurantById(Long id) {
        return restaurantRepository.findById(id);
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findByIsActiveTrue();
    }

    public List<Restaurant> getRestaurantsByOwner(String ownerId) {
        return restaurantRepository.findByOwnerId(ownerId);
    }

    public List<Restaurant> getRestaurantsByCuisine(String cuisine) {
        return restaurantRepository.findByCuisine(cuisine);
    }

    public Restaurant updateRestaurant(Long id, Restaurant restaurantDetails) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        restaurant.setName(restaurantDetails.getName());
        restaurant.setDescription(restaurantDetails.getDescription());
        restaurant.setAddress(restaurantDetails.getAddress());
        restaurant.setPhoneNumber(restaurantDetails.getPhoneNumber());
        restaurant.setEmail(restaurantDetails.getEmail());
        restaurant.setCuisine(restaurantDetails.getCuisine());
        restaurant.setOpeningHours(restaurantDetails.getOpeningHours());
        restaurant.setClosingHours(restaurantDetails.getClosingHours());
        restaurant.setImageUrl(restaurantDetails.getImageUrl());

        return restaurantRepository.save(restaurant);
    }

    public void deleteRestaurant(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        restaurant.setIsActive(false);
        restaurantRepository.save(restaurant);
    }

    public MenuItem addMenuItem(Long restaurantId, MenuItem menuItem) {
        menuItem.setRestaurantId(restaurantId);
        menuItem.setIsAvailable(true);
        return menuRepository.save(menuItem);
    }

    public List<MenuItem> getMenuItems(Long restaurantId) {
        return menuRepository.findByRestaurantId(restaurantId);
    }
}
