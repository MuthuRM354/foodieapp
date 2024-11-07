package com.foodieapp.favorite.repository;

import com.foodieapp.favorite.model.FavoriteItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FavoriteItemRepository extends MongoRepository<FavoriteItem, String> {
    List<FavoriteItem> findByUserId(String userId);
    List<FavoriteItem> findByUserIdAndRestaurantId(String userId, Long restaurantId);
    void deleteByUserIdAndItemId(String userId, String itemId);
}
