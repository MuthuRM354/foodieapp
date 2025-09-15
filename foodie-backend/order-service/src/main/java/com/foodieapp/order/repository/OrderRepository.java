package com.foodieapp.order.repository;

import com.foodieapp.order.model.Order;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserId(String userId, Sort sort);
    List<Order> findByRestaurantId(String restaurantId, Sort sort);
}
