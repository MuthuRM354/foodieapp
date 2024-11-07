package com.foodieapp.user.service;

import com.foodieapp.user.model.User;
import com.foodieapp.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void verifyUser(String id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setVerified(true);
        userRepository.save(user);
    }

    public List<User> getRestaurantOwners() {
        return userRepository.findByRole("RESTAURANT_OWNER");
    }
}
