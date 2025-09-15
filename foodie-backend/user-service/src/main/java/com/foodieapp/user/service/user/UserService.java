package com.foodieapp.user.service.user;

import com.foodieapp.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    User findUserByJwtToken(String jwt);
    User findUserByEmail(String email);
    User findUserById(String userId);
    User findUserByUsername(String username); // Added this method
    List<User> findAllUsers();

    /**
     * Find all users with pagination
     * @param pageable Pagination information
     * @return Page of User objects
     */
    Page<User> findAllUsers(Pageable pageable);

    User updateUser(User user, String jwt);
    void updateVerificationStatus(String type, String identifier);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsById(String userId);
    void changePassword(String userId, String currentPassword, String newPassword);
    User deactivateAccount(String userId, String jwt);
    User deactivateAccountByAdmin(String userId, String adminJwt);
    User reactivateAccount(String userId, String adminJwt);
    void deleteUserPermanently(String userId, String jwt);
    void deleteUserPermanentlyByAdmin(String userId, String adminJwt);
    User approveRestaurantOwner(String userId, String adminJwt);
    /**
     * Verify email and activate user account
     * @param userId User ID
     * @param token Verification token
     * @return Activated user
     */
    User verifyEmailAndActivateUser(String userId, String token);
    /**
     * Store profile picture and return the URL
     * @param userId User ID
     * @param file Multipart file containing the image
     * @return URL where the picture is stored
     */
    String storeProfilePicture(String userId, MultipartFile file) throws IOException;
    Long countAllUsers();
    Long countUsersByRole(String roleName);
    Long countPendingRestaurantOwners();
    Long countNewUsersInLastDays(int days);
    Long countActiveUsers();
}
