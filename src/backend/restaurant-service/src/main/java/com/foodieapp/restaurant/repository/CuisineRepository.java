package com.foodieapp.restaurant.repository;

import com.foodieapp.restaurant.model.Cuisine;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CuisineRepository extends MongoRepository<Cuisine, String> {
    List<Cuisine> findByIsActiveTrue();
    List<Cuisine> findByNameContainingIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
