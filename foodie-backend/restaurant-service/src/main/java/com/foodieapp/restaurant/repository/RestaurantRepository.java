package com.foodieapp.restaurant.repository;

import com.foodieapp.restaurant.model.Restaurant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RestaurantRepository extends MongoRepository<Restaurant, String> {
    List<Restaurant> findByIsActiveTrue();
    List<Restaurant> findByOwnerId(String ownerId);
    List<Restaurant> findByCuisine(String cuisine);
    List<Restaurant> findByNameContainingIgnoreCase(String name);
    List<Restaurant> findByIsVerifiedFalse();

    // Add more specific queries to reduce MongoTemplate usage
    @Query("{ 'isActive': true, 'isVerified': true }")
    List<Restaurant> findActiveAndVerified(Pageable pageable);

    @Query("{ 'isActive': true, 'isVerified': true, 'avgRating': { $gte: ?0 } }")
    List<Restaurant> findTopRated(double minRating, Pageable pageable);

    @Query("{ 'isActive': true, 'isVerified': true, 'id': { $nin: ?0 } }")
    List<Restaurant> findActiveVerifiedNotIn(List<String> excludeIds, Pageable pageable);
}
