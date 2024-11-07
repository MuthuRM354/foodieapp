package com.foodieapp.user.service;

import com.foodieapp.user.model.User;
import com.foodieapp.user.model.UserRole;
import com.foodieapp.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class RestaurantOwnerService {
    @Autowired
    private UserRepository userRepository;

    public User registerRestaurantOwner(User user) {
        user.setRole(UserRole.RESTAURANT_OWNER);
        user.setIsVerified(false);
        return userRepository.save(user);
    }

    public User getOwnerProfile(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant owner not found"));
    }

    public User updateOwnerProfile(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant owner not found"));

        user.setBusinessName(userDetails.getBusinessName());
        user.setBusinessAddress(userDetails.getBusinessAddress());
        user.setBusinessLicense(userDetails.getBusinessLicense());
        user.setPhoneNumber(userDetails.getPhoneNumber());

        return userRepository.save(user);
    }

    public List<User> getPendingVerifications() {
        return userRepository.findByRoleAndIsVerified(UserRole.RESTAURANT_OWNER, false);
    }

    public List<User> getVerifiedOwners() {
        return userRepository.findByRoleAndIsVerified(UserRole.RESTAURANT_OWNER, true);
    }

    public User updateBusinessInfo(Long id, Map<String, Object> updates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant owner not found"));

        if (updates.containsKey("businessName")) {
            user.setBusinessName((String) updates.get("businessName"));
        }
        if (updates.containsKey("businessAddress")) {
            user.setBusinessAddress((String) updates.get("businessAddress"));
        }
        if (updates.containsKey("businessLicense")) {
            user.setBusinessLicense((String) updates.get("businessLicense"));
        }

        return userRepository.save(user);
    }

    public List<User> searchOwners(String query) {
        return userRepository.findByRoleAndBusinessNameContaining(UserRole.RESTAURANT_OWNER, query);
    }
}
