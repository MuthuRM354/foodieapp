package com.foodieapp.favorite.controller;

import com.foodieapp.favorite.model.FavoriteRestaurant;
import com.foodieapp.favorite.model.FavoriteCuisine;
import com.foodieapp.favorite.model.FavoriteItem;
import com.foodieapp.favorite.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // Restaurant favorites endpoints
    @PostMapping("/restaurants")
    public ResponseEntity<FavoriteRestaurant> addFavoriteRestaurant(@RequestBody FavoriteRestaurant favorite) {
        return ResponseEntity.ok(favoriteService.addFavoriteRestaurant(favorite));
    }

    @GetMapping("/restaurants/user/{userId}")
    public ResponseEntity<List<FavoriteRestaurant>> getUserFavoriteRestaurants(@PathVariable String userId) {
        return ResponseEntity.ok(favoriteService.getUserFavoriteRestaurants(userId));
    }

    @DeleteMapping("/restaurants/{userId}/{restaurantId}")
    public ResponseEntity<Void> removeFavoriteRestaurant(
            @PathVariable String userId,
            @PathVariable Long restaurantId) {
        favoriteService.removeFavoriteRestaurant(userId, restaurantId);
        return ResponseEntity.ok().build();
    }

    // Cuisine favorites endpoints
    @PostMapping("/cuisines")
    public ResponseEntity<FavoriteCuisine> addFavoriteCuisine(@RequestBody FavoriteCuisine favorite) {
        return ResponseEntity.ok(favoriteService.addFavoriteCuisine(favorite));
    }

    @GetMapping("/cuisines/user/{userId}")
    public ResponseEntity<List<FavoriteCuisine>> getUserFavoriteCuisines(@PathVariable String userId) {
        return ResponseEntity.ok(favoriteService.getUserFavoriteCuisines(userId));
    }

    @DeleteMapping("/cuisines/{userId}/{cuisineId}")
    public ResponseEntity<Void> removeFavoriteCuisine(
            @PathVariable String userId,
            @PathVariable String cuisineId) {
        favoriteService.removeFavoriteCuisine(userId, cuisineId);
        return ResponseEntity.ok().build();
    }

    // Menu item favorites endpoints
    @PostMapping("/items")
    public ResponseEntity<FavoriteItem> addFavoriteItem(@RequestBody FavoriteItem favorite) {
        return ResponseEntity.ok(favoriteService.addFavoriteItem(favorite));
    }

    @GetMapping("/items/user/{userId}")
    public ResponseEntity<List<FavoriteItem>> getUserFavoriteItems(@PathVariable String userId) {
        return ResponseEntity.ok(favoriteService.getUserFavoriteItems(userId));
    }

    @GetMapping("/items/user/{userId}/restaurant/{restaurantId}")
    public ResponseEntity<List<FavoriteItem>> getUserFavoriteItemsByRestaurant(
            @PathVariable String userId,
            @PathVariable Long restaurantId) {
        return ResponseEntity.ok(favoriteService.getUserFavoriteItemsByRestaurant(userId, restaurantId));
    }

    @DeleteMapping("/items/{userId}/{itemId}")
    public ResponseEntity<Void> removeFavoriteItem(
            @PathVariable String userId,
            @PathVariable String itemId) {
        favoriteService.removeFavoriteItem(userId, itemId);
        return ResponseEntity.ok().build();
    }
}
