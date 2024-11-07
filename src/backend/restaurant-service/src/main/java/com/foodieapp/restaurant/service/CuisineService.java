package com.foodieapp.restaurant.service;

import com.foodieapp.restaurant.model.Cuisine;
import com.foodieapp.restaurant.repository.CuisineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CuisineService {
    @Autowired
    private CuisineRepository cuisineRepository;

    public List<Cuisine> getAllCuisines() {
        return cuisineRepository.findByIsActiveTrue();
    }

    public Cuisine createCuisine(Cuisine cuisine) {
        if (cuisineRepository.existsByNameIgnoreCase(cuisine.getName())) {
            throw new RuntimeException("Cuisine with this name already exists");
        }
        cuisine.setCreatedAt(LocalDateTime.now());
        return cuisineRepository.save(cuisine);
    }

    public Cuisine updateCuisine(String id, Cuisine cuisineDetails) {
        Cuisine cuisine = getCuisineById(id);
        cuisine.setName(cuisineDetails.getName());
        cuisine.setDescription(cuisineDetails.getDescription());
        cuisine.setImageUrl(cuisineDetails.getImageUrl());
        cuisine.setUpdatedAt(LocalDateTime.now());
        return cuisineRepository.save(cuisine);
    }

    public void deleteCuisine(String id) {
        Cuisine cuisine = getCuisineById(id);
        cuisine.setIsActive(false);
        cuisine.setUpdatedAt(LocalDateTime.now());
        cuisineRepository.save(cuisine);
    }

    public Cuisine getCuisineById(String id) {
        return cuisineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuisine not found"));
    }

    public List<Cuisine> searchCuisines(String query) {
        return cuisineRepository.findByNameContainingIgnoreCase(query);
    }
}

