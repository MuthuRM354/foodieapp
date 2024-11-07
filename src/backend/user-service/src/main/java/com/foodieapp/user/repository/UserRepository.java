package com.foodieapp.user.repository;

import com.foodieapp.user.model.User;
import com.foodieapp.user.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
    List<User> findByRoleAndIsVerified(UserRole role, boolean isVerified);
    List<User> findByRoleAndFirstNameContainingOrLastNameContaining(UserRole role, String firstName, String lastName);
    List<User> findByRoleAndBusinessNameContaining(UserRole role, String businessName);
}
