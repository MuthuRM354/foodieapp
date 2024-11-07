package com.foodieapp.user.controller;

import com.foodieapp.user.model.User;
import com.foodieapp.user.service.CustomerService;
import com.foodieapp.user.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private ImageUploadService imageUploadService;

    @PostMapping("/register")
    public ResponseEntity<User> registerCustomer(@RequestBody User user) {
        return ResponseEntity.ok(customerService.registerCustomer(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateProfile(@PathVariable String id, @RequestBody User user) {
        return ResponseEntity.ok(customerService.updateCustomerProfile(id, user));
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        String imageUrl = imageUploadService.uploadImage(file, id);
        return ResponseEntity.ok(imageUrl);
    }
}