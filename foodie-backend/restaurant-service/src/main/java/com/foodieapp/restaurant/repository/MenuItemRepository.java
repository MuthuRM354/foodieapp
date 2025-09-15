package com.foodieapp.restaurant.repository;

import com.foodieapp.restaurant.model.MenuItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MenuItemRepository extends MongoRepository<MenuItem, String> {
    List<MenuItem> findByRestaurantId(String restaurantId);
    List<MenuItem> findByRestaurantIdAndCategory(String restaurantId, String category);
    List<MenuItem> findByRestaurantIdAndIsAvailableTrue(String restaurantId);
    MenuItem findByIdAndRestaurantId(String id, String restaurantId);
}
