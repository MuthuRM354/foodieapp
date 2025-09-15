package com.foodieapp.restaurant.service;

import com.foodieapp.restaurant.exception.RestaurantNotFoundException;
import com.foodieapp.restaurant.exception.UnauthorizedException;
import com.foodieapp.restaurant.model.ImageData;
import com.foodieapp.restaurant.model.MenuItem;
import com.foodieapp.restaurant.model.Restaurant;
import com.foodieapp.restaurant.repository.ImageRepository;
import com.foodieapp.restaurant.repository.MenuItemRepository;
import com.foodieapp.restaurant.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class ImageService {
    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    // Image type constants
    public static final String IMAGE_TYPE_RESTAURANT = "RESTAURANT";
    public static final String IMAGE_TYPE_MENU_ITEM = "MENU_ITEM";

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private RestaurantOwnerVerificationService ownerVerificationService;

    /**
     * Upload restaurant image
     */
    public String uploadRestaurantImage(String restaurantId, MultipartFile file) throws IOException {
        // Verify restaurant exists
        Restaurant restaurant = verifyRestaurantExists(restaurantId);

        // Verify permission
        ownerVerificationService.requireRestaurantOwnership(restaurantId);

        // Delete existing image if present
        deleteExistingImage(IMAGE_TYPE_RESTAURANT, restaurantId);

        // Create and save new image
        ImageData imageData = createImageData(file, restaurant.getOwnerId(), restaurantId, null, IMAGE_TYPE_RESTAURANT);
        imageData = imageRepository.save(imageData);

        // Update restaurant with image URL
        updateRestaurantImageUrl(restaurant, imageData.getId());

        return imageData.getId();
    }

    /**
     * Upload menu item image
     */
    public String uploadMenuItemImage(String restaurantId, String menuItemId, MultipartFile file) throws IOException {
        // Verify restaurant exists
        Restaurant restaurant = verifyRestaurantExists(restaurantId);

        // Verify permission
        ownerVerificationService.requireRestaurantOwnership(restaurantId);

        // Verify menu item exists
        MenuItem menuItem = verifyMenuItemExists(restaurantId, menuItemId);

        // Delete existing image if present
        deleteExistingImage(IMAGE_TYPE_MENU_ITEM, menuItemId);

        // Create and save new image
        ImageData imageData = createImageData(file, restaurant.getOwnerId(), restaurantId, menuItemId, IMAGE_TYPE_MENU_ITEM);
        imageData = imageRepository.save(imageData);

        // Update menu item with image URL
        updateMenuItemImageUrl(menuItem, imageData.getId());

        return imageData.getId();
    }

    /**
     * Get image by ID
     */
    public ImageData getImage(String imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found"));
    }

    /**
     * Delete an image
     */
    public void deleteImage(String imageId) {
        ImageData image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found"));

        // Verify permission to delete
        String currentUserId = authorizationService.getCurrentUserId();
        if (!image.getOwnerId().equals(currentUserId) && !authorizationService.isCurrentUserAdmin()) {
            throw new UnauthorizedException("You don't have permission to delete this image");
        }

        imageRepository.delete(image);
    }

    // Private helper methods to make the code more modular

    private Restaurant verifyRestaurantExists(String restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
    }

    private MenuItem verifyMenuItemExists(String restaurantId, String menuItemId) {
        MenuItem menuItem = menuItemRepository.findByIdAndRestaurantId(menuItemId, restaurantId);
        if (menuItem == null) {
            throw new IllegalArgumentException("Menu item not found");
        }
        return menuItem;
    }

    private void deleteExistingImage(String imageType, String entityId) {
        Optional<ImageData> existingImage;

        if (IMAGE_TYPE_RESTAURANT.equals(imageType)) {
            existingImage = imageRepository.findByRestaurantId(entityId);
        } else if (IMAGE_TYPE_MENU_ITEM.equals(imageType)) {
            existingImage = imageRepository.findByMenuItemId(entityId);
        } else {
            throw new IllegalArgumentException("Unsupported image type: " + imageType);
        }

        existingImage.ifPresent(image -> imageRepository.delete(image));
    }

    private ImageData createImageData(MultipartFile file, String ownerId, String restaurantId,
                                      String menuItemId, String imageType) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        ImageData imageData = new ImageData();
        imageData.setName(file.getOriginalFilename());
        imageData.setType(file.getContentType());
        imageData.setData(file.getBytes());
        imageData.setRestaurantId(restaurantId);
        imageData.setOwnerId(ownerId);
        imageData.setImageType(imageType);

        if (IMAGE_TYPE_MENU_ITEM.equals(imageType)) {
            imageData.setMenuItemId(menuItemId);
        }

        return imageData;
    }

    private void updateRestaurantImageUrl(Restaurant restaurant, String imageId) {
        String imageUrl = "/api/v1/images/" + imageId;
        restaurant.setImageUrl(imageUrl);
        restaurantRepository.save(restaurant);
    }

    private void updateMenuItemImageUrl(MenuItem menuItem, String imageId) {
        String imageUrl = "/api/v1/images/" + imageId;
        menuItem.setImageUrl(imageUrl);
        menuItemRepository.save(menuItem);
    }
}
