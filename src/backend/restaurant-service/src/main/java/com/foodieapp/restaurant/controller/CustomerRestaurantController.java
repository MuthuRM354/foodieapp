package com.foodieapp.restaurant.controller;

import com.foodieapp.restaurant.model.Restaurant;
import com.foodieapp.restaurant.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/customer/restaurants")
public class CustomerRestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/location/{location}")
    public ResponseEntity<List<Restaurant>> getByLocation(@PathVariable String location) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByLocation(location));
    }

    @GetMapping("/cuisine/{cuisine}")
    public ResponseEntity<List<Restaurant>> getByCuisine(@PathVariable String cuisine) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByCuisine(cuisine));
    }
}
