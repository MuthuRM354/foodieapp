package com.foodieapp.restaurant.controller;

import com.foodieapp.restaurant.dto.response.ApiResponse;
import com.foodieapp.restaurant.model.ImageData;
import com.foodieapp.restaurant.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    /**
     * Upload restaurant image
     */
    @PostMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse<String>> uploadRestaurantImage(
            @PathVariable String restaurantId,
            @RequestParam("file") MultipartFile file) {
        try {
            String imageId = imageService.uploadRestaurantImage(restaurantId, file);
            return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", imageId));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to upload image: " + e.getMessage(), "UPLOAD_ERROR"));
        }
    }

    /**
     * Upload menu item image
     */
    @PostMapping("/restaurant/{restaurantId}/menu/{menuItemId}")
    public ResponseEntity<ApiResponse<String>> uploadMenuItemImage(
            @PathVariable String restaurantId,
            @PathVariable String menuItemId,
            @RequestParam("file") MultipartFile file) {
        try {
            String imageId = imageService.uploadMenuItemImage(restaurantId, menuItemId, file);
            return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", imageId));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to upload image: " + e.getMessage(), "UPLOAD_ERROR"));
        }
    }

    /**
     * Get image by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable String id) {
        ImageData image = imageService.getImage(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(image.getType()));
        headers.setContentLength(image.getData().length);
        return new ResponseEntity<>(image.getData(), headers, HttpStatus.OK);
    }

    /**
     * Delete image
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable String id) {
        imageService.deleteImage(id);
        return ResponseEntity.ok(ApiResponse.success("Image deleted successfully", null));
    }
}