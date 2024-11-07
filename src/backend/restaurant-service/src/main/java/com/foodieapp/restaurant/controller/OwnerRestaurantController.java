package com.foodieapp.restaurant.controller;

import com.foodieapp.restaurant.model.Restaurant;
import com.foodieapp.restaurant.model.MenuItem;
import com.foodieapp.restaurant.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/owner/restaurants")
public class OwnerRestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant) {
        return ResponseEntity.ok(restaurantService.createRestaurant(restaurant));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(
            @PathVariable String id,
            @RequestBody Restaurant restaurant) {
        return ResponseEntity.ok(restaurantService.updateRestaurant(id, restaurant));
    }

    @PutMapping("/{id}/menu")
    public ResponseEntity<Restaurant> updateMenu(
            @PathVariable String id,
            @RequestBody List<MenuItem> menu) {
        return ResponseEntity.ok(restaurantService.updateMenu(id, menu));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Restaurant>> getOwnerRestaurants(@PathVariable String ownerId) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByOwner(ownerId));
    }
}
