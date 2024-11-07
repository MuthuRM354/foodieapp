package com.foodieapp.user.repository;

import com.foodieapp.user.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    List<User> findByRole(String role);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}