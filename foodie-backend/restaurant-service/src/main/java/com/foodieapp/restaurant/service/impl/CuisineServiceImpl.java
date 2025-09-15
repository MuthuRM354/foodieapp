package com.foodieapp.restaurant.service.impl;

import com.foodieapp.restaurant.dto.response.CuisineDTO;
import com.foodieapp.restaurant.model.Cuisine;
import com.foodieapp.restaurant.service.CuisineService;
import com.foodieapp.restaurant.service.external.ThirdPartyApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CuisineServiceImpl implements CuisineService {
    private static final Logger logger = LoggerFactory.getLogger(CuisineServiceImpl.class);

    private final ThirdPartyApiClient thirdPartyApiClient;

    @Autowired
    public CuisineServiceImpl(ThirdPartyApiClient thirdPartyApiClient) {
        this.thirdPartyApiClient = thirdPartyApiClient;
    }

    @Override
    @Cacheable("popularCuisines")
    public List<CuisineDTO> getPopularCuisines() {
        logger.info("Fetching popular cuisines");
        List<Cuisine> allCuisines = getAllCuisines();

        // For demo purposes - hard-coding popular cuisines
        List<String> popularCuisineNames = Arrays.asList(
                "Italian", "Chinese", "Indian", "Mexican", "Japanese", "Thai", "American"
        );

        return allCuisines.stream()
                .filter(cuisine -> popularCuisineNames.contains(cuisine.getName()))
                .limit(10)
                .map(this::mapToCuisineDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable("allCuisines")
    public List<Cuisine> getAllCuisines() {
        logger.info("Fetching all cuisines from API");
        return thirdPartyApiClient.getCuisineTypes();
    }

    @Override
    @Cacheable("cuisineNames")
    public List<String> getAllCuisineNames() {
        return getAllCuisines().stream()
                .map(Cuisine::getName)
                .collect(Collectors.toList());
    }

    private CuisineDTO mapToCuisineDTO(Cuisine cuisine) {
        CuisineDTO dto = new CuisineDTO();
        dto.setId(cuisine.getId());
        dto.setName(cuisine.getName());
        dto.setDescription(cuisine.getDescription());
        dto.setImageUrl(cuisine.getImageUrl());
        return dto;
    }
}
