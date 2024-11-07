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
    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{id}/verify")
    public ResponseEntity<Void> verifyUser(@PathVariable String id) {
        adminService.verifyUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/restaurant-owners")
    public ResponseEntity<List<User>> getRestaurantOwners() {
        return ResponseEntity.ok(adminService.getRestaurantOwners());
    }
}
