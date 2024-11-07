package com.foodieapp.user.controller;

import com.foodieapp.user.model.User;
import com.foodieapp.user.service.RestaurantOwnerService;
import com.foodieapp.user.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/restaurant-owners")
public class RestaurantOwnerController {
    @Autowired
    private RestaurantOwnerService ownerService;

    @Autowired
    private ImageUploadService imageUploadService;

    @PostMapping("/register")
    public ResponseEntity<User> registerOwner(@RequestBody User user) {
        return ResponseEntity.ok(ownerService.registerOwner(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateProfile(@PathVariable String id, @RequestBody User user) {
        return ResponseEntity.ok(ownerService.updateOwnerProfile(id, user));
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        String imageUrl = imageUploadService.uploadImage(file, id);
        return ResponseEntity.ok(imageUrl);
    }
}