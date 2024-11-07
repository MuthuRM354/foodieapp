package com.foodieapp.user.controller;

import com.foodieapp.user.model.User;
import com.foodieapp.user.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/restaurant-owners")
    public ResponseEntity<List<User>> getRestaurantOwners() {
        return ResponseEntity.ok(adminService.getRestaurantOwners());
    }

    @PutMapping("/verify/{userId}")
    public ResponseEntity<Void> verifyRestaurantOwner(@PathVariable Long userId) {
        adminService.verifyRestaurantOwner(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/deactivate/{userId}")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long userId) {
        adminService.deactivateUser(userId);
        return ResponseEntity.ok().build();
    }
}
