package com.foodieapp.user.service;

import com.foodieapp.user.model.User;
import com.foodieapp.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    @Autowired
    private UserRepository userRepository;

    public User registerCustomer(User user) {
        user.setRole("CUSTOMER");
        user.setVerified(true);
        return userRepository.save(user);
    }

    public User updateCustomerProfile(String id, User user) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setId(id);
        user.setRole("CUSTOMER");
        return userRepository.save(user);
    }
}
