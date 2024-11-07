package com.foodieapp.restaurant.repository;

import com.foodieapp.restaurant.model.Restaurant;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface RestaurantRepository extends MongoRepository<Restaurant, String> {
    List<Restaurant> findByLocation(String location);
    List<Restaurant> findByCuisine(String cuisine);
    List<Restaurant> findByOwnerId(String ownerId);
    List<Restaurant> findByStatus(String status);
}