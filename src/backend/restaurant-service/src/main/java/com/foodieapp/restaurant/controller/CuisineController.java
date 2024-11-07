package com.foodieapp.restaurant.controller;

import com.foodieapp.restaurant.model.Cuisine;
import com.foodieapp.restaurant.service.CuisineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cuisines")
public class CuisineController {
    @Autowired
    private CuisineService cuisineService;

    @GetMapping("/{id}")
    public ResponseEntity<Cuisine> getCuisine(@PathVariable String id) {
        return ResponseEntity.ok(cuisineService.getCuisineById(id));
    }

    @PostMapping
    public ResponseEntity<Cuisine> createCuisine(@RequestBody Cuisine cuisine) {
        return ResponseEntity.ok(cuisineService.createCuisine(cuisine));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cuisine> updateCuisine(@PathVariable String id, @RequestBody Cuisine cuisine) {
        return ResponseEntity.ok(cuisineService.updateCuisine(id, cuisine));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCuisine(@PathVariable String id) {
        cuisineService.deleteCuisine(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Cuisine>> searchCuisines(@RequestParam String query) {
        return ResponseEntity.ok(cuisineService.searchCuisines(query));
    }
}
