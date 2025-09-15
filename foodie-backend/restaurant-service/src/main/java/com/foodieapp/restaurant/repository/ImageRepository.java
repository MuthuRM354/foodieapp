package com.foodieapp.restaurant.repository;

import com.foodieapp.restaurant.model.ImageData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends MongoRepository<ImageData, String> {
    Optional<ImageData> findByRestaurantId(String restaurantId);
    Optional<ImageData> findByMenuItemId(String menuItemId);
    void deleteByRestaurantId(String restaurantId);
    void deleteByMenuItemId(String menuItemId);
}