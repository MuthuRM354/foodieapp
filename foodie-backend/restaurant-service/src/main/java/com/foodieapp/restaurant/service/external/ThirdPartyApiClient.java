package com.foodieapp.restaurant.service.external;

import com.foodieapp.restaurant.model.Cuisine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ThirdPartyApiClient {
    private static final Logger logger = LoggerFactory.getLogger(ThirdPartyApiClient.class);

    private final RestTemplate restTemplate;
    private final String spoonacularApiUrl;
    private final String spoonacularApiKey;

    public ThirdPartyApiClient(
            RestTemplate restTemplate,
            @Value("${spoonacular.api.url:https://api.spoonacular.com}") String spoonacularApiUrl,
            @Value("${spoonacular.api.key:demo-key}") String spoonacularApiKey) {
        this.restTemplate = restTemplate;
        this.spoonacularApiUrl = spoonacularApiUrl;
        this.spoonacularApiKey = spoonacularApiKey;
    }

    public List<Cuisine> getCuisineTypes() {
        try {
            String endpoint = spoonacularApiUrl + "/food/cuisines";
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-api-key", spoonacularApiKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String[]> response = restTemplate.exchange(
                    endpoint, HttpMethod.GET, entity, String[].class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return convertToCuisines(Arrays.asList(response.getBody()));
            }
        } catch (Exception e) {
            logger.error("Error fetching cuisine types from Spoonacular API", e);
        }

        // Return default cuisines if API fails
        return getDefaultCuisines();
    }

    public List<String> getIngredients() {
        try {
            String endpoint = spoonacularApiUrl + "/food/ingredients/search?number=100";
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-api-key", spoonacularApiKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<IngredientsResponse> response = restTemplate.exchange(
                    endpoint, HttpMethod.GET, entity, IngredientsResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return extractIngredientNames(response.getBody());
            }
        } catch (Exception e) {
            logger.error("Error fetching ingredients from Spoonacular API", e);
        }

        // Return default ingredients if API fails
        return getDefaultIngredients();
    }

    private List<Cuisine> convertToCuisines(List<String> cuisineNames) {
        return cuisineNames.stream()
                .map(name -> {
                    Cuisine cuisine = new Cuisine();
                    cuisine.setName(name);
                    cuisine.setDescription("Cuisine type: " + name);
                    cuisine.setIsActive(true);
                    cuisine.setCreatedAt(LocalDateTime.now());
                    return cuisine;
                })
                .collect(Collectors.toList());
    }

    private List<Cuisine> getDefaultCuisines() {
        String[] defaultCuisines = {
                "Italian", "Chinese", "Indian", "Mexican", "Thai",
                "Japanese", "French", "Greek", "Spanish", "Lebanese"
        };

        return convertToCuisines(Arrays.asList(defaultCuisines));
    }

    private List<String> extractIngredientNames(IngredientsResponse response) {
        if (response.results == null) return Collections.emptyList();

        return response.results.stream()
                .map(ingredient -> ingredient.name)
                .collect(Collectors.toList());
    }

    private List<String> getDefaultIngredients() {
        return Arrays.asList(
                "Chicken", "Beef", "Pork", "Tofu", "Rice",
                "Pasta", "Tomatoes", "Onions", "Garlic", "Bell Peppers",
                "Cheese", "Milk", "Eggs", "Flour", "Sugar"
        );
    }

    private static class IngredientsResponse {
        public List<Ingredient> results;
    }

    private static class Ingredient {
        public String name;
        public long id;
    }
}