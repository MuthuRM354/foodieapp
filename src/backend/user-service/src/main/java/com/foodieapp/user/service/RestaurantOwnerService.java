package com.foodieapp.user.service;

import com.foodieapp.user.model.User;
import com.foodieapp.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestaurantOwnerService {
    @Autowired
    private UserRepository userRepository;

    public User registerOwner(User user) {
        user.setRole("RESTAURANT_OWNER");
        user.setVerified(false);
        return userRepository.save(user);
    }

    public User updateOwnerProfile(String id, User user) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setId(id);
        user.setRole("RESTAURANT_OWNER");
        user.setVerified(existingUser.isVerified());
        return userRepository.save(user);
    }
}
