package com.foodieapp.user.service.user;

import com.foodieapp.user.exception.ResourceNotFoundException;
import com.foodieapp.user.exception.UnauthorizedException;
import com.foodieapp.user.exception.ValidationException;
import com.foodieapp.user.exception.TokenExpiredException;
import com.foodieapp.user.model.Role;
import com.foodieapp.user.model.User;
import com.foodieapp.user.repository.UserRepository;
import com.foodieapp.user.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Helper method to validate admin privileges and return admin user
     */
    private User validateAdminAndGetUser(String adminJwt) {
        User admin = findUserByJwtToken(adminJwt);
        if (!admin.hasRole(Role.ROLE_ADMIN)) {
            throw new UnauthorizedException("Only admins can perform this action");
        }
        return admin;
    }

    @Override
    public User findUserByJwtToken(String jwt) {
        String email = jwtService.extractUsername(jwt);
        return findUserByEmail(email);
    }

    @Override
    @Cacheable(value = "users", key = "#email")
    public User findUserByEmail(String email) {
        logger.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });
    }

    @Override
    @Cacheable(value = "users", key = "#userId")
    public User findUserById(String userId) {
        logger.debug("Finding user by ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId);
                });
    }

    @Override
    @Cacheable(value = "users", key = "'username_' + #username")
    public User findUserByUsername(String username) {
        logger.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found with username: {}", username);
                    return new ResourceNotFoundException("User not found with username: " + username);
                });
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> findAllUsers(Pageable pageable) {
        logger.debug("Finding all users with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findAll(pageable);
    }

    @Override
    @CacheEvict(value = "users", key = "#user.id")
    public User updateUser(User user, String jwt) {
        User existingUser = findUserByJwtToken(jwt);

        if (!existingUser.getId().equals(user.getId())) {
            logger.warn("User {} attempted to update profile of user {}", existingUser.getId(), user.getId());
            throw new UnauthorizedException("You can only update your own profile");
        }

        // Update only allowed fields
        existingUser.setFullName(user.getFullName());
        existingUser.setProfilePictureUrl(user.getProfilePictureUrl());
        // Don't update email or password through this method

        User savedUser = userRepository.save(existingUser);
        logger.info("User profile updated for ID: {}", existingUser.getId());
        return savedUser;
    }

    @Override
    @CacheEvict(value = "users", key = "#userId")
    public void changePassword(String userId, String currentPassword, String newPassword) {
        User user = findUserById(userId);

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            logger.warn("Failed password change attempt for user {}: incorrect current password", userId);
            throw new UnauthorizedException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        logger.info("Password changed for user: {}", userId);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void updateVerificationStatus(String type, String identifier) {
        User user = null;

        if ("phone".equals(type)) {
            user = userRepository.findByPhoneNumber(identifier);
            if (user != null) {
                user.setPhoneVerified(true);
                user.setEnabled(true); // Enable account after phone verification
                userRepository.save(user);
                logger.info("Phone verified for user: {}", identifier);
            } else {
                logger.warn("User not found for phone verification: {}", identifier);
            }
        } else {
            logger.warn("Invalid verification type: {}", type);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsById(String userId) {
        return userRepository.existsById(userId);
    }


    @CacheEvict(value = "users", key = "#userId")
    public void changePassword(String userId, String currentPassword, String newPassword, String confirmPassword) {
        User user = findUserById(userId);

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            logger.warn("Failed password change attempt for user {}: incorrect current password", userId);
            throw new UnauthorizedException("Current password is incorrect");
        }

        // Verify passwords match
        if (!newPassword.equals(confirmPassword)) {
            logger.warn("Failed password change attempt for user {}: passwords don't match", userId);
            throw new ValidationException("New password and confirmation do not match");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        logger.info("Password changed for user: {}", userId);
    }

    @Override
    @CacheEvict(value = "users", key = "#userId")
    public User deactivateAccount(String userId, String jwt) {
        User requestingUser = findUserByJwtToken(jwt);
        User targetUser = findUserById(userId);

        if (!requestingUser.getId().equals(targetUser.getId())) {
            logger.warn("User {} attempted to deactivate account of user {}", requestingUser.getId(), userId);
            throw new UnauthorizedException("You can only deactivate your own account");
        }

        targetUser.setEnabled(false);
        User savedUser = userRepository.save(targetUser);

        logger.info("Account deactivated for user: {}", userId);
        return savedUser;
    }

    @Override
    @CacheEvict(value = "users", key = "#userId")
    public User deactivateAccountByAdmin(String userId, String adminJwt) {
        User admin = validateAdminAndGetUser(adminJwt);
        User targetUser = findUserById(userId);
        targetUser.setEnabled(false);
        User savedUser = userRepository.save(targetUser);

        logger.info("Account deactivated for user: {} by admin: {}", userId, admin.getId());
        return savedUser;
    }

    @Override
    @CacheEvict(value = "users", key = "#userId")
    public User reactivateAccount(String userId, String adminJwt) {
        User admin = validateAdminAndGetUser(adminJwt);
        User targetUser = findUserById(userId);
        targetUser.setEnabled(true);
        targetUser.setAccountLocked(false);
        targetUser.setFailedLoginAttempts(0);
        User savedUser = userRepository.save(targetUser);

        logger.info("Account reactivated for user: {} by admin: {}", userId, admin.getId());
        return savedUser;
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void deleteUserPermanently(String userId, String jwt) {
        User requestingUser = findUserByJwtToken(jwt);
        User targetUser = findUserById(userId);

        // Check if the requesting user is trying to delete their own account
        if (!requestingUser.getId().equals(targetUser.getId())) {
            logger.warn("User {} attempted to delete account of user {}", requestingUser.getId(), userId);
            throw new UnauthorizedException("You can only delete your own account");
        }

        // Perform the actual deletion
        userRepository.deleteById(userId);

        logger.info("User account permanently deleted: {}", userId);
    }

    @Override
    @CacheEvict(value = "users", key = "#userId")
    public User approveRestaurantOwner(String userId, String adminJwt) {
        User admin = validateAdminAndGetUser(adminJwt);
        User user = findUserById(userId);

        // Check if the user has requested to be a restaurant owner
        if (!user.hasRole(Role.ROLE_RESTAURANT_OWNER)) {
            logger.warn("Admin {} attempted to approve non-restaurant owner {}", admin.getId(), userId);
            throw new ResourceNotFoundException("User is not a restaurant owner");
        }

        // Update verification status to mark as approved
        user.setEmailVerified(true);
        user.setPhoneVerified(true);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        logger.info("Restaurant owner approved: {} by admin: {}", userId, admin.getId());
        return savedUser;
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void deleteUserPermanentlyByAdmin(String userId, String adminJwt) {
        User admin = validateAdminAndGetUser(adminJwt);

        // Check that admin is not trying to delete themselves
        if (admin.getId().equals(userId)) {
            logger.warn("Admin {} attempted to delete their own account", admin.getId());
            throw new UnauthorizedException("Admins cannot delete their own accounts");
        }

        // Verify the user exists
        if (!userRepository.existsById(userId)) {
            logger.warn("Admin {} attempted to delete non-existent user {}", admin.getId(), userId);
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        // Perform the actual deletion
        userRepository.deleteById(userId);

        logger.info("User account permanently deleted by admin: {} for user: {}", admin.getId(), userId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public User verifyEmailAndActivateUser(String userId, String token) {
        // Validate token
        if (!jwtService.isTokenValid(token)) {
            throw new TokenExpiredException("Verification token has expired");
        }

        // Find user
        User user = findUserById(userId);

        // Verify token belongs to this user
        String emailFromToken = jwtService.extractUsername(token);
        if (!user.getEmail().equals(emailFromToken)) {
            throw new UnauthorizedException("Invalid verification token");
        }

        // Mark email as verified
        user.setEmailVerified(true);

        // Only activate regular customers automatically
        // Restaurant owners need admin approval
        if (!user.hasRole(Role.ROLE_RESTAURANT_OWNER)) {
            user.setEnabled(true);
            logger.info("Email verified and user activated: {}", userId);
        } else {
            logger.info("Restaurant owner email verified: {}. Awaiting admin approval.", userId);
        }

        User savedUser = userRepository.save(user);
        return savedUser;
    }

    @Value("${app.user.profile-pictures.path:profile-pictures}")
    private String profilePicturesPath;

    @Value("${app.user.profile-pictures.base-url:${server.servlet.context-path}/profile-pictures}")
    private String profilePicturesBaseUrl;

    @Override
    public String storeProfilePicture(String userId, MultipartFile file) throws IOException {
        // Create directory if it doesn't exist
        File directory = new File(profilePicturesPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generate unique filename
        String filename = userId + "_" + System.currentTimeMillis() + "_" +
                StringUtils.cleanPath(file.getOriginalFilename());

        // Save the file
        Path targetLocation = Paths.get(profilePicturesPath).resolve(filename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Return the URL
        return profilePicturesBaseUrl + "/" + filename;
    }

    @Override
    public Long countAllUsers() {
        return userRepository.count();
    }

    @Override
    public Long countUsersByRole(String roleName) {
        return userRepository.countByRoleName(roleName);
    }

    @Override
    public Long countPendingRestaurantOwners() {
        return userRepository.countPendingByRoleName(Role.ROLE_RESTAURANT_OWNER);
    }

    @Override
    public Long countNewUsersInLastDays(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return userRepository.countByCreatedAtAfter(cutoffDate);
    }

    @Override
    public Long countActiveUsers() {
        return userRepository.countByEnabledTrue();
    }
}
