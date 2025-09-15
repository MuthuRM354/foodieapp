package com.foodieapp.order.repository;

import com.foodieapp.order.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends MongoRepository<Cart, String> {
    Optional<Cart> findByUserId(String userId);
    Optional<Cart> findByUserIdAndIsActiveTrue(String userId);
    List<Cart> findByRestaurantId(String restaurantId);

    @Query("{'lastUpdated': {$lt: ?0}}")
    List<Cart> findInactiveCarts(LocalDateTime threshold);

    @Query("{'isActive': true, 'lastUpdated': {$lt: ?0}}")
    List<Cart> findAbandonedCarts(LocalDateTime threshold);

    void deleteByUserIdAndIsActiveFalse(String userId);

    List<Cart> findByIsActiveTrue();

    @Query("{'items.itemId': ?0}")
    List<Cart> findCartsContainingItem(String itemId);
}
