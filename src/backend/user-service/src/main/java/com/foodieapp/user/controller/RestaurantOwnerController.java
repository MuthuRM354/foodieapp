package com.foodieapp.user.controller;

import com.foodieapp.user.model.User;
import com.foodieapp.user.service.RestaurantOwnerService;
import com.foodieapp.user.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurant-owners")
public class RestaurantOwnerController {
    @Autowired
    private RestaurantOwnerService restaurantOwnerService;

    @Autowired
    private ImageUploadService imageUploadService;

    @PostMapping("/register")
    public ResponseEntity<User> registerOwner(@RequestBody User user) {
        return ResponseEntity.ok(restaurantOwnerService.registerRestaurantOwner(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantOwnerService.getOwnerProfile(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateProfile(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(restaurantOwnerService.updateOwnerProfile(id, user));
    }

    @PostMapping("/{id}/documents")
    public ResponseEntity<String> uploadDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        String documentUrl = imageUploadService.uploadDocument(file, id);
        return ResponseEntity.ok(documentUrl);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<User>> getPendingVerifications() {
        return ResponseEntity.ok(restaurantOwnerService.getPendingVerifications());
    }

    @GetMapping("/verified")
    public ResponseEntity<List<User>> getVerifiedOwners() {
        return ResponseEntity.ok(restaurantOwnerService.getVerifiedOwners());
    }

    @PutMapping("/{id}/business-info")
    public ResponseEntity<User> updateBusinessInfo(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(restaurantOwnerService.updateBusinessInfo(id, updates));
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchOwners(@RequestParam String query) {
        return ResponseEntity.ok(restaurantOwnerService.searchOwners(query));
    }
}
