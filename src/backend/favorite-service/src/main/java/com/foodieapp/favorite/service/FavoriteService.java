package com.foodieapp.favorite.service;

import com.foodieapp.favorite.model.FavoriteRestaurant;
import com.foodieapp.favorite.model.FavoriteCuisine;
import com.foodieapp.favorite.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;

    public FavoriteRestaurant addFavoriteRestaurant(FavoriteRestaurant favorite) {
        return favoriteRepository.save(favorite);
    }

    public FavoriteCuisine addFavoriteCuisine(FavoriteCuisine favorite) {
        return favoriteRepository.save(favorite);
    }

    public List<FavoriteRestaurant> getUserFavoriteRestaurants(String userId) {
        return favoriteRepository.findRestaurantsByUserId(userId);
    }

    public List<FavoriteCuisine> getUserFavoriteCuisines(String userId) {
        return favoriteRepository.findCuisinesByUserId(userId);
    }

    public void removeFavoriteRestaurant(String userId, String restaurantId) {
        favoriteRepository.deleteByUserIdAndRestaurantId(userId, restaurantId);
    }

    public void removeFavoriteCuisine(String userId, String cuisineId) {
        favoriteRepository.deleteByUserIdAndCuisineId(userId, cuisineId);
    }
}