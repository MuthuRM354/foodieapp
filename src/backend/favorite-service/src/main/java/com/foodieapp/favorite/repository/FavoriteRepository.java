package com.foodieapp.favorite.repository;

import com.foodieapp.favorite.model.FavoriteRestaurant;
import com.foodieapp.favorite.model.FavoriteCuisine;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface FavoriteRepository extends MongoRepository<Object, String> {
    List<FavoriteRestaurant> findRestaurantsByUserId(String userId);
    List<FavoriteCuisine> findCuisinesByUserId(String userId);
    void deleteByUserIdAndRestaurantId(String userId, String restaurantId);
    void deleteByUserIdAndCuisineId(String userId, String cuisineId);
}
