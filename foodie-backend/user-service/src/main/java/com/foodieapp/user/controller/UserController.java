package com.foodieapp.user.controller;

import com.foodieapp.user.dto.request.PasswordChangeRequest;
import com.foodieapp.user.dto.request.UpdateUserRequest;
import com.foodieapp.user.dto.response.ApiResponse;
import com.foodieapp.user.dto.response.UserDTO;
import com.foodieapp.user.exception.ValidationException;
import com.foodieapp.user.model.User;
import com.foodieapp.user.service.user.UserService;
import com.foodieapp.user.util.ResponseUtil;
import com.foodieapp.user.util.TokenUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final TokenUtil tokenUtil;

    @Autowired
    public UserController(UserService userService, TokenUtil tokenUtil) {
        this.userService = userService;
        this.tokenUtil = tokenUtil;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> getUserProfile(@RequestHeader("Authorization") String jwt) {
        logger.debug("Received profile request");
        User user = tokenUtil.getUserFromToken(jwt);
        UserDTO userDTO = UserDTO.fromUser(user);
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved successfully", userDTO));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserProfile(
            @Valid @RequestBody UpdateUserRequest updateRequest,
            @RequestHeader("Authorization") String jwt) {
        logger.debug("Received profile update request");
        String token = tokenUtil.extractToken(jwt);
        User currentUser = tokenUtil.getUserFromToken(jwt);

        // Update allowed fields
        currentUser.setFullName(updateRequest.getFullName());
        if (updateRequest.getProfilePictureUrl() != null) {
            currentUser.setProfilePictureUrl(updateRequest.getProfilePictureUrl());
        }

        // Save updated user
        User updatedUser = userService.updateUser(currentUser, token);
        UserDTO userDTO = UserDTO.fromUser(updatedUser);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", userDTO));
    }

    @PostMapping("/profile/picture")
    public ResponseEntity<ApiResponse<UserDTO>> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String jwt) {

        logger.debug("Received profile picture upload request");

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Please select a file to upload"));
        }

        try {
            User user = tokenUtil.getUserFromToken(jwt);
            String profilePictureUrl = userService.storeProfilePicture(user.getId(), file);

            // Update user with the new profile picture URL
            user.setProfilePictureUrl(profilePictureUrl);
            User updatedUser = userService.updateUser(user, tokenUtil.extractToken(jwt));

            UserDTO userDTO = UserDTO.fromUser(updatedUser);
            return ResponseUtil.success("Profile picture updated successfully", userDTO);
        } catch (Exception e) {
            logger.error("Error uploading profile picture: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to upload profile picture: " + e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody PasswordChangeRequest request,
            @RequestHeader("Authorization") String jwt) {

        // Validate password confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException("New password and confirmation do not match");
        }

        User user = tokenUtil.getUserFromToken(jwt);
        userService.changePassword(
            user.getId(),
            request.getCurrentPassword(),
            request.getNewPassword());

        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    @PostMapping("/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateAccount(@RequestHeader("Authorization") String jwt) {
        String token = tokenUtil.extractToken(jwt);
        User user = tokenUtil.getUserFromToken(jwt);

        userService.deactivateAccount(user.getId(), token);

        return ResponseEntity.ok(ApiResponse.success("Account deactivated successfully", null));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@RequestHeader("Authorization") String jwt) {
        logger.info("Received request to permanently delete user account");
        String token = tokenUtil.extractToken(jwt);
        User user = tokenUtil.getUserFromToken(jwt);

        userService.deleteUserPermanently(user.getId(), token);

        return ResponseEntity.ok(ApiResponse.success("Account permanently deleted", null));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable String userId) {
        logger.debug("Fetching user with id: {}", userId);
        User user = userService.findUserById(userId);
        UserDTO userDTO = UserDTO.fromUser(user);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", userDTO));
    }
}
