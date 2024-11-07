package com.foodieapp.restaurant.service;

import com.foodieapp.restaurant.model.Restaurant;
import com.foodieapp.restaurant.model.MenuItem;
import com.foodieapp.restaurant.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public Restaurant updateRestaurant(String id, Restaurant restaurant) {
        restaurant.setId(id);
        return restaurantRepository.save(restaurant);
    }

    public Restaurant updateMenu(String id, List<MenuItem> menu) {
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        restaurant.setMenu(menu);
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> getRestaurantsByLocation(String location) {
        return restaurantRepository.findByLocation(location);
    }

    public List<Restaurant> getRestaurantsByCuisine(String cuisine) {
        return restaurantRepository.findByCuisine(cuisine);
    }

    public List<Restaurant> getRestaurantsByOwner(String ownerId) {
        return restaurantRepository.findByOwnerId(ownerId);
    }

    public Restaurant updateStatus(String id, String status) {
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        restaurant.setStatus(status);
        return restaurantRepository.save(restaurant);
    }
}