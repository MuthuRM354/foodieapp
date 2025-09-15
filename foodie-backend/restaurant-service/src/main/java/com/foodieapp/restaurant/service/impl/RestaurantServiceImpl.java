package com.foodieapp.restaurant.service.impl;

import com.foodieapp.restaurant.dto.response.CuisineDTO;
import com.foodieapp.restaurant.dto.response.RestaurantDTO;
import com.foodieapp.restaurant.dto.response.RestaurantDetailResponse;
import com.foodieapp.restaurant.exception.RestaurantNotFoundException;
import com.foodieapp.restaurant.model.Cuisine;
import com.foodieapp.restaurant.model.MenuItem;
import com.foodieapp.restaurant.model.Restaurant;
import com.foodieapp.restaurant.repository.MenuItemRepository;
import com.foodieapp.restaurant.repository.RestaurantRepository;
import com.foodieapp.restaurant.service.AuthorizationService;
import com.foodieapp.restaurant.service.CuisineService;
import com.foodieapp.restaurant.service.RestaurantOwnerVerificationService;
import com.foodieapp.restaurant.service.RestaurantService;
import com.foodieapp.restaurant.service.external.ThirdPartyApiClient;
import com.foodieapp.restaurant.service.notification.NotificationService;
import com.foodieapp.restaurant.util.mapper.EntityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImpl implements RestaurantService {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantServiceImpl.class);

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final MongoTemplate mongoTemplate;
    private final ThirdPartyApiClient thirdPartyApiClient;
    private final AuthorizationService authorizationService;
    private final NotificationService notificationService;
    private final CuisineService cuisineService;
    private final RestaurantOwnerVerificationService ownerVerificationService;

    @Autowired
    public RestaurantServiceImpl(
            RestaurantRepository restaurantRepository,
            MenuItemRepository menuItemRepository,
            MongoTemplate mongoTemplate,
            ThirdPartyApiClient thirdPartyApiClient,
            AuthorizationService authorizationService,
            NotificationService notificationService,
            @Lazy CuisineService cuisineService,
            @Lazy RestaurantOwnerVerificationService ownerVerificationService) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.mongoTemplate = mongoTemplate;
        this.thirdPartyApiClient = thirdPartyApiClient;
        this.authorizationService = authorizationService;
        this.notificationService = notificationService;
        this.cuisineService = cuisineService;
        this.ownerVerificationService = ownerVerificationService;
    }

    @Override
    public RestaurantDetailResponse createRestaurant(Restaurant restaurant) {
        // Validate required fields
        if (restaurant.getName() == null || restaurant.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Restaurant name is required");
        }
        if (restaurant.getAddress() == null || restaurant.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Restaurant address is required");
        }
        if (restaurant.getOwnerId() == null || restaurant.getOwnerId().trim().isEmpty()) {
            throw new IllegalArgumentException("Restaurant owner ID is required");
        }

        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant.setIsActive(true);
        restaurant.setIsVerified(false);
        return EntityMapper.toRestaurantResponse(restaurantRepository.save(restaurant));
    }

    @Override
    public Restaurant getRestaurantById(String id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));
    }

    @Override
    public RestaurantDetailResponse getRestaurantDetailById(String id) {
        Restaurant restaurant = getRestaurantById(id);
        return EntityMapper.toRestaurantResponse(restaurant);
    }

    @Override
    public List<RestaurantDetailResponse> getAllRestaurants() {
        return restaurantRepository.findByIsActiveTrue().stream()
                .map(EntityMapper::toRestaurantResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RestaurantDetailResponse> getRestaurantsByOwner(String ownerId) {
        return restaurantRepository.findByOwnerId(ownerId).stream()
                .map(EntityMapper::toRestaurantResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RestaurantDetailResponse> getRestaurantsByCuisine(String cuisine) {
        return restaurantRepository.findByCuisine(cuisine).stream()
                .map(EntityMapper::toRestaurantResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RestaurantDetailResponse updateRestaurant(String id, Restaurant restaurantDetails) {
        Restaurant restaurant = getRestaurantById(id);
        updateRestaurantFields(restaurant, restaurantDetails);
        return EntityMapper.toRestaurantResponse(restaurantRepository.save(restaurant));
    }

    private void updateRestaurantFields(Restaurant restaurant, Restaurant restaurantDetails) {
        restaurant.setName(restaurantDetails.getName());
        restaurant.setDescription(restaurantDetails.getDescription());
        restaurant.setAddress(restaurantDetails.getAddress());
        restaurant.setPhoneNumber(restaurantDetails.getPhoneNumber());
        restaurant.setEmail(restaurantDetails.getEmail());
        restaurant.setCuisine(restaurantDetails.getCuisine());
        restaurant.setOpeningHours(restaurantDetails.getOpeningHours());
        restaurant.setClosingHours(restaurantDetails.getClosingHours());
        restaurant.setImageUrl(restaurantDetails.getImageUrl());
        restaurant.setIsVerified(restaurantDetails.getIsVerified());
        restaurant.setUpdatedAt(LocalDateTime.now());

        // If restaurant is verified, send notification
        if (Boolean.TRUE.equals(restaurantDetails.getIsVerified()) &&
                (restaurant.getIsVerified() == null || !restaurant.getIsVerified())) {
            try {
                // Get owner details and send notification
                Map<String, Object> ownerDetails = authorizationService.getUserDetails(restaurant.getOwnerId());
                if (ownerDetails != null && ownerDetails.containsKey("email")) {
                    notificationService.sendRestaurantVerificationNotification(restaurant);
                }
            } catch (Exception e) {
                logger.error("Error sending verification notification: {}", e.getMessage(), e);
                // Continue processing even if notification fails
            }
        }
    }

    @Override
    public void deleteRestaurant(String id) {
        Restaurant restaurant = getRestaurantById(id);
        restaurant.setIsActive(false);
        restaurant.setUpdatedAt(LocalDateTime.now());
        restaurantRepository.save(restaurant);
    }

    @Override
    public MenuItem addMenuItem(String restaurantId, MenuItem menuItem) {
        // Verify restaurant exists
        Restaurant restaurant = getRestaurantById(restaurantId);

        // Validate required fields
        if (menuItem.getName() == null || menuItem.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Menu item name is required");
        }
        if (menuItem.getPrice() == null) {
            throw new IllegalArgumentException("Menu item price is required");
        }

        menuItem.setRestaurantId(restaurantId);
        return menuItemRepository.save(menuItem);
    }

    @Override
    public MenuItem getMenuItem(String restaurantId, String itemId) {
        // Verify restaurant exists
        getRestaurantById(restaurantId);

        MenuItem menuItem = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found with id: " + itemId));

        // Verify the item belongs to the restaurant
        if (!menuItem.getRestaurantId().equals(restaurantId)) {
            throw new IllegalArgumentException("Menu item does not belong to this restaurant");
        }

        return menuItem;
    }

    @Override
    public MenuItem updateMenuItem(String restaurantId, String itemId, MenuItem menuItem) {
        // Verify restaurant exists
        Restaurant restaurant = getRestaurantById(restaurantId);

        // Verify menu item exists
        MenuItem existingItem = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found with id: " + itemId));

        // Verify the item belongs to the restaurant
        if (!existingItem.getRestaurantId().equals(restaurantId)) {
            throw new IllegalArgumentException("Menu item does not belong to this restaurant");
        }

        // Update properties but keep restaurant ID and item ID
        menuItem.setRestaurantId(restaurantId);
        menuItem.setId(itemId);

        MenuItem updated = menuItemRepository.save(menuItem);

        try {
            // Send notification if needed
            notificationService.sendMenuItemUpdateNotification(restaurant, updated);
        } catch (Exception e) {
            logger.error("Error sending menu item update notification: {}", e.getMessage(), e);
            // Continue processing even if notification fails
        }

        return updated;
    }

    @Override
    public void deleteMenuItem(String restaurantId, String itemId) {
        // Verify restaurant exists
        getRestaurantById(restaurantId);

        // Verify menu item exists
        MenuItem existingItem = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found with id: " + itemId));

        // Verify the item belongs to the restaurant
        if (!existingItem.getRestaurantId().equals(restaurantId)) {
            throw new IllegalArgumentException("Menu item does not belong to this restaurant");
        }

        menuItemRepository.deleteById(itemId);
    }

    @Override
    public List<MenuItem> getMenuItems(String restaurantId) {
        // Verify restaurant exists
        getRestaurantById(restaurantId);
        return menuItemRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public List<RestaurantDetailResponse> getPopularRestaurants(int limit) {
        logger.info("Fetching popular restaurants with limit: {}", limit);
        // Use repository method instead of MongoTemplate
        List<Restaurant> restaurants = restaurantRepository.findActiveAndVerified(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "avgRating", "orderCount"))
        );

        return restaurants.stream()
                .map(EntityMapper::toRestaurantResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RestaurantDetailResponse> getNearbyRestaurants(
            double latitude, double longitude, double radiusInKm, int limit) {
        logger.info("Fetching nearby restaurants at lat: {}, lng: {}, radius: {}km, limit: {}",
                latitude, longitude, radiusInKm, limit);

        Point location = new Point(longitude, latitude);
        Distance distance = new Distance(radiusInKm, Metrics.KILOMETERS);

        Query query = new Query();
        query.addCriteria(Criteria.where("location").nearSphere(location).maxDistance(distance.getNormalizedValue())
                .and("isActive").is(true)
                .and("isVerified").is(true));
        query.limit(limit);

        List<Restaurant> restaurants = mongoTemplate.find(query, Restaurant.class);
        return restaurants.stream()
                .map(EntityMapper::toRestaurantResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RestaurantDetailResponse> getNewRestaurants(int limit) {
        logger.info("Fetching new restaurants with limit: {}", limit);
        // Use repository method instead of MongoTemplate
        List<Restaurant> restaurants = restaurantRepository.findActiveAndVerified(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        return restaurants.stream()
                .map(EntityMapper::toRestaurantResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RestaurantDetailResponse> getTopRatedRestaurants(int limit) {
        logger.info("Fetching top rated restaurants with limit: {}", limit);
        // Use repository method instead of MongoTemplate
        List<Restaurant> restaurants = restaurantRepository.findTopRated(
                4.0, PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "avgRating"))
        );

        return restaurants.stream()
                .map(EntityMapper::toRestaurantResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RestaurantDTO> getFeaturedRestaurants() {
        // For simplicity, combine popular and top-rated restaurants
        List<Restaurant> featuredRestaurants = new ArrayList<>();

        try {
            // Get top 5 popular restaurants by order count
            List<Restaurant> popularRestaurants = restaurantRepository.findActiveAndVerified(
                    PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "orderCount"))
            );
            featuredRestaurants.addAll(popularRestaurants);

            // Get top 5 rated restaurants not already in the list
            List<String> existingIds = featuredRestaurants.stream()
                    .map(Restaurant::getId)
                    .collect(Collectors.toList());

            List<Restaurant> topRatedRestaurants = restaurantRepository.findActiveVerifiedNotIn(
                    existingIds, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "avgRating"))
            );
            featuredRestaurants.addAll(topRatedRestaurants);

            // Map to DTOs
            return featuredRestaurants.stream()
                    .map(EntityMapper::toRestaurantDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching featured restaurants: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}
