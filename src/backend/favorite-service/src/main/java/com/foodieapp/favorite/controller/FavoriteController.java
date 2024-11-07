package com.foodieapp.favorite.controller;

import com.foodieapp.favorite.model.FavoriteRestaurant;
import com.foodieapp.favorite.model.FavoriteCuisine;
import com.foodieapp.favorite.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/restaurants")
    public ResponseEntity<FavoriteRestaurant> addFavoriteRestaurant(@RequestBody FavoriteRestaurant favorite) {
        return ResponseEntity.ok(favoriteService.addFavoriteRestaurant(favorite));
    }

    @PostMapping("/cuisines")
    public ResponseEntity<FavoriteCuisine> addFavoriteCuisine(@RequestBody FavoriteCuisine favorite) {
        return ResponseEntity.ok(favoriteService.addFavoriteCuisine(favorite));
    }

    @GetMapping("/restaurants/{userId}")
    public ResponseEntity<List<FavoriteRestaurant>> getUserFavoriteRestaurants(@PathVariable String userId) {
        return ResponseEntity.ok(favoriteService.getUserFavoriteRestaurants(userId));
    }

    @GetMapping("/cuisines/{userId}")
    public ResponseEntity<List<FavoriteCuisine>> getUserFavoriteCuisines(@PathVariable String userId) {
        return ResponseEntity.ok(favoriteService.getUserFavoriteCuisines(userId));
    }

    @DeleteMapping("/restaurants/{userId}/{restaurantId}")
    public ResponseEntity<Void> removeFavoriteRestaurant(
            @PathVariable String userId,
            @PathVariable String restaurantId) {
        favoriteService.removeFavoriteRestaurant(userId, restaurantId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cuisines/{userId}/{cuisineId}")
    public ResponseEntity<Void> removeFavoriteCuisine(
            @PathVariable String userId,
            @PathVariable String cuisineId) {
        favoriteService.removeFavoriteCuisine(userId, cuisineId);
        return ResponseEntity.ok().build();
    }
}