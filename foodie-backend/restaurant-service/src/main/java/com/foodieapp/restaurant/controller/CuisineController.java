package com.foodieapp.restaurant.controller;

import com.foodieapp.restaurant.dto.response.ApiResponse;
import com.foodieapp.restaurant.dto.response.CuisineDTO;
import com.foodieapp.restaurant.model.Cuisine;
import com.foodieapp.restaurant.service.AuthorizationService;
import com.foodieapp.restaurant.service.CuisineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cuisines")
public class CuisineController {

    private static final Logger logger = LoggerFactory.getLogger(CuisineController.class);

    @Autowired
    private CuisineService cuisineService;

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<CuisineDTO>>> getPopularCuisines() {
        try {
            List<CuisineDTO> cuisines = cuisineService.getPopularCuisines();
            return ResponseEntity.ok(ApiResponse.success("Popular cuisines retrieved successfully",
                cuisines.isEmpty() ? new ArrayList<>() : cuisines));
        } catch (Exception e) {
            logger.error("Error retrieving popular cuisines: {}", e.getMessage(), e);
            return ResponseEntity.ok(
                ApiResponse.error("Failed to retrieve popular cuisines", "INTERNAL_SERVER_ERROR"));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<String>>> getAllCuisineNames() {
        try {
            List<String> cuisineNames = cuisineService.getAllCuisineNames();
            return ResponseEntity.ok(ApiResponse.success("All cuisine names retrieved successfully", cuisineNames));
        } catch (Exception e) {
            logger.error("Error retrieving cuisine names: {}", e.getMessage(), e);
            return ResponseEntity.ok(
                ApiResponse.error("Failed to retrieve cuisine names", "INTERNAL_SERVER_ERROR"));
        }
    }

    @GetMapping("/admin/all")
    public ResponseEntity<ApiResponse<List<Cuisine>>> getAllCuisinesForAdmin() {
        try {
            // Use centralized authorization
            authorizationService.requireAdminRole();

            List<Cuisine> cuisines = cuisineService.getAllCuisines();
            return ResponseEntity.ok(ApiResponse.success("All cuisines retrieved successfully", cuisines));
        } catch (Exception e) {
            logger.error("Error retrieving all cuisines: {}", e.getMessage(), e);
            return ResponseEntity.ok(
                ApiResponse.error("Failed to retrieve all cuisines", "INTERNAL_SERVER_ERROR"));
        }
    }
}