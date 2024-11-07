package com.foodieapp.restaurant.service;

import com.foodieapp.restaurant.model.Cuisine;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CuisineService {
    private final MongoRepository<Cuisine, String> cuisineRepository;

    public CuisineService(MongoRepository<Cuisine, String> cuisineRepository) {
        this.cuisineRepository = cuisineRepository;
    }

    public List<Cuisine> getAllCuisines() {
        return cuisineRepository.findAll();
    }

    public Cuisine addCuisine(Cuisine cuisine) {
        return cuisineRepository.save(cuisine);
    }

    public void deleteCuisine(String id) {
        cuisineRepository.deleteById(id);
    }
}
