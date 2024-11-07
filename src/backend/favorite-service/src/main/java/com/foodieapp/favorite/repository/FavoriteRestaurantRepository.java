package com.foodieapp.favorite.repository;

import com.foodieapp.favorite.model.FavoriteRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRestaurantRepository extends JpaRepository<FavoriteRestaurant, Long> {
    List<FavoriteRestaurant> findByUserId(String userId);
    Optional<FavoriteRestaurant> findByUserIdAndRestaurantId(String userId, Long restaurantId);
    void deleteByUserIdAndRestaurantId(String userId, Long restaurantId);
}
