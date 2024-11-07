package com.foodieapp.restaurant.controller;

import com.foodieapp.restaurant.model.Restaurant;
import com.foodieapp.restaurant.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/restaurants")
public class AdminRestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @PutMapping("/{id}/status")
    public ResponseEntity<Restaurant> updateStatus(
            @PathVariable String id,
            @RequestParam String status) {
        return ResponseEntity.ok(restaurantService.updateStatus(id, status));
    }
}
