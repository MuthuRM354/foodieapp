package com.foodieapp.user.service;

import com.foodieapp.user.model.User;
import com.foodieapp.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        return userRepository.save(user);
    }

    public User updateUser(String id, User user) {
        user.setId(id);
        return userRepository.save(user);
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void verifyUser(String id) {
        User user = getUserById(id);
        user.setVerified(true);
        userRepository.save(user);
    }
}