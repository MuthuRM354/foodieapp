package com.foodieapp.order.repository;

import com.foodieapp.order.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserId(String userId);
    List<Order> findByRestaurantId(String restaurantId);
    List<Order> findByStatus(String status);
}