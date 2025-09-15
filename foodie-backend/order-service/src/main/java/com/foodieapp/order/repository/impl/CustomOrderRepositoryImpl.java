package com.foodieapp.order.repository.impl;

import com.foodieapp.order.model.Order;
import com.foodieapp.order.model.OrderStatus;
import com.foodieapp.order.repository.CustomOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CustomOrderRepositoryImpl implements CustomOrderRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Order> findOrdersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        Query query = new Query();
        query.addCriteria(Criteria.where("orderTime").gte(startDate).lte(endDate));
        return mongoTemplate.find(query, Order.class);
    }

    @Override
    public List<Order> findOrdersByStatusForRestaurant(String restaurantId, OrderStatus status) {
        Query query = new Query();
        query.addCriteria(Criteria.where("restaurantId").is(restaurantId)
                .and("status").is(status));
        return mongoTemplate.find(query, Order.class);
    }

    @Override
    public List<Order> findUndeliveredOrdersOlderThan(LocalDateTime threshold) {
        Query query = new Query();
        query.addCriteria(Criteria.where("orderTime").lt(threshold)
                .and("status").nin(OrderStatus.DELIVERED, OrderStatus.CANCELLED));
        return mongoTemplate.find(query, Order.class);
    }

    @Override
    public long countOrdersByUserRestaurant(String userId, String restaurantId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId)
                .and("restaurantId").is(restaurantId));
        return mongoTemplate.count(query, Order.class);
    }
}