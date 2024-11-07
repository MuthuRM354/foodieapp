package com.foodieapp.user.service;

import com.foodieapp.user.model.User;
import com.foodieapp.user.model.UserRole;
import com.foodieapp.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;

    @Autowired
    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getRestaurantOwners() {
        return userRepository.findByRole(UserRole.RESTAURANT_OWNER);
    }

    public void verifyRestaurantOwner(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() == UserRole.RESTAURANT_OWNER) {
            user.setIsVerified(true);
            userRepository.save(user);
        }
    }

    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
    }
}
