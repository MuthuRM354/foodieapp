package com.foodieapp.restaurant.service;

import com.foodieapp.restaurant.dto.response.CuisineDTO;
import com.foodieapp.restaurant.model.Cuisine;
import java.util.List;

public interface CuisineService {
    // Consolidated cuisine methods
    List<CuisineDTO> getPopularCuisines();
    List<Cuisine> getAllCuisines();
    List<String> getAllCuisineNames();
}