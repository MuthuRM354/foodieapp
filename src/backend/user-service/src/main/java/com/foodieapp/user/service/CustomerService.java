package com.foodieapp.user.service;

import com.foodieapp.user.model.User;
import com.foodieapp.user.model.UserRole;
import com.foodieapp.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomerService {
    @Autowired
    private UserRepository userRepository;

    public User registerCustomer(User user) {
        user.setRole(UserRole.CUSTOMER);
        user.setIsVerified(false);
        return userRepository.save(user);
    }

    public User getCustomerProfile(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public User updateCustomerProfile(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        user.setDefaultAddress(userDetails.getDefaultAddress());

        return userRepository.save(user);
    }

    public void deleteCustomer(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        user.setIsActive(false);
        userRepository.save(user);
    }

    public List<User> searchCustomers(String query) {
        return userRepository.findByRoleAndFirstNameContainingOrLastNameContaining(
                UserRole.CUSTOMER, query, query);
    }

    public void updatePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        // Add password validation and hashing logic
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    public void verifyEmail(Long id, String token) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        // Add email verification logic
        user.setIsVerified(true);
        userRepository.save(user);
    }
}