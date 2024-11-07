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

    @GetMapping("/{id}")
    public ResponseEntity<User> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerProfile(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateProfile(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(customerService.updateCustomerProfile(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        String imageUrl = imageUploadService.uploadImage(file);
        User user = customerService.getCustomerProfile(id);
        user.setProfileImage(imageUrl);
        customerService.updateCustomerProfile(id, user);
        return ResponseEntity.ok(imageUrl);
    }

    @PostMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        customerService.updatePassword(id, oldPassword, newPassword);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<Void> verifyEmail(
            @PathVariable Long id,
            @RequestParam String token) {
        customerService.verifyEmail(id, token);
        return ResponseEntity.ok().build();
    }
}
