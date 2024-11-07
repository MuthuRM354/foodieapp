package com.foodieapp.restaurant.repository;

import com.foodieapp.restaurant.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByIsActiveTrue();
    List<Restaurant> findByOwnerId(String ownerId);
    List<Restaurant> findByCuisine(String cuisine);
    List<Restaurant> findByNameContainingIgnoreCase(String name);
}
