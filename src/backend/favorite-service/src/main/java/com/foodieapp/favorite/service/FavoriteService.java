package com.foodieapp.favorite.service;

import com.foodieapp.favorite.model.*;
import com.foodieapp.favorite.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoriteService {
    @Autowired
    private FavoriteRestaurantRepository favoriteRestaurantRepository;
    @Autowired
    private FavoriteCuisineRepository favoriteCuisineRepository;
    @Autowired
    private FavoriteItemRepository favoriteItemRepository;

    // Restaurant favorites
    public FavoriteRestaurant addFavoriteRestaurant(FavoriteRestaurant favorite) {
        favorite.setCreatedAt(LocalDateTime.now());
        return favoriteRestaurantRepository.save(favorite);
    }

    public List<FavoriteRestaurant> getUserFavoriteRestaurants(String userId) {
        return favoriteRestaurantRepository.findByUserId(userId);
    }

    public void removeFavoriteRestaurant(String userId, Long restaurantId) {
        favoriteRestaurantRepository.deleteByUserIdAndRestaurantId(userId, restaurantId);
    }

    // Cuisine favorites
    public FavoriteCuisine addFavoriteCuisine(FavoriteCuisine favorite) {
        favorite.setCreatedAt(LocalDateTime.now());
        return favoriteCuisineRepository.save(favorite);
    }

    public List<FavoriteCuisine> getUserFavoriteCuisines(String userId) {
        return favoriteCuisineRepository.findByUserId(userId);
    }

    public void removeFavoriteCuisine(String userId, String cuisineId) {
        favoriteCuisineRepository.deleteByUserIdAndCuisineId(userId, cuisineId);
    }

    // Item favorites
    public FavoriteItem addFavoriteItem(FavoriteItem favorite) {
        favorite.setCreatedAt(LocalDateTime.now());
        return favoriteItemRepository.save(favorite);
    }

    public List<FavoriteItem> getUserFavoriteItems(String userId) {
        return favoriteItemRepository.findByUserId(userId);
    }

    public List<FavoriteItem> getUserFavoriteItemsByRestaurant(String userId, Long restaurantId) {
        return favoriteItemRepository.findByUserIdAndRestaurantId(userId, restaurantId);
    }

    public void removeFavoriteItem(String userId, String itemId) {
        favoriteItemRepository.deleteByUserIdAndItemId(userId, itemId);
    }
}
