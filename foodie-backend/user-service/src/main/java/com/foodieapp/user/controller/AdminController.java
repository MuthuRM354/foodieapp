package com.foodieapp.user.controller;

import com.foodieapp.user.dto.request.CreateUserRequest;
import com.foodieapp.user.dto.response.ApiResponse;
import com.foodieapp.user.dto.response.UserDTO;
import com.foodieapp.user.model.Role;
import com.foodieapp.user.model.User;
import com.foodieapp.user.security.JwtService;
import com.foodieapp.user.service.auth.RegistrationService;
import com.foodieapp.user.service.user.UserService;
import com.foodieapp.user.util.ResponseUtil;
import com.foodieapp.user.util.TokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "APIs for admin operations")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final UserService userService;
    private final JwtService jwtService;
    private final TokenUtil tokenUtil;
    private final RegistrationService registrationService;

    public AdminController(UserService userService, JwtService jwtService, TokenUtil tokenUtil, RegistrationService registrationService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.tokenUtil = tokenUtil;
        this.registrationService = registrationService;
    }

    @GetMapping("/users")
    @Operation(
            summary = "Get all users",
            description = "Retrieves a paginated list of all users in the system"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getAllUsers(
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "id") String sort,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "asc") String direction) {

        // Uses the consolidated method from TokenUtil
        tokenUtil.validateAdminAccess(authHeader);

        // Map special sort terms to actual entity properties
        String sortProperty = sort;
        if ("newest".equals(sort)) {
            sortProperty = "createdAt";
        } else if ("oldest".equals(sort)) {
            sortProperty = "createdAt";
            // Flip the direction for "oldest"
            direction = "asc".equalsIgnoreCase(direction) ? "desc" : "asc";
        }

        // Create sort object with the mapped property
        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(dir, sortProperty);

        // Create pageable request
        Pageable pageable = PageRequest.of(page, size, sortObj);

        // Get paginated users
        Page<User> users = userService.findAllUsers(pageable);
        Page<UserDTO> userDTOs = users.map(UserDTO::fromUser);

        return ResponseUtil.success("Users retrieved successfully", userDTOs);
    }

    @PostMapping("/create-admin")
    @Operation(
            summary = "Create new admin user",
            description = "Creates a new admin user with full administrative privileges"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Admin user created successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            )
    })
    public ResponseEntity<ApiResponse<ApiResponse.AuthResponse>> createAdmin(
            @Valid @RequestBody CreateUserRequest request,
            @RequestHeader("Authorization") String authHeader) {

        // Validate the current user has admin access
        tokenUtil.validateAdminAccess(authHeader);

        logger.info("Creating new admin user with email: {}", request.getEmail());

        // Register the new admin user
        ApiResponse.AuthResponse authResponse = registrationService.registerAdmin(request);

        return ResponseUtil.success("Admin user created successfully", authResponse);
    }

    @PostMapping("/approve-restaurant-owner/{userId}")
    @Operation(
            summary = "Approve restaurant owner",
            description = "Approves a restaurant owner account"
    )
    public ResponseEntity<ApiResponse<UserDTO>> approveRestaurantOwner(
            @PathVariable String userId,
            @RequestHeader("Authorization") String authHeader) {

        // Uses the consolidated method from TokenUtil
        tokenUtil.validateAdminAccess(authHeader);

        // Approve the restaurant owner
        String token = tokenUtil.extractToken(authHeader);
        User user = userService.approveRestaurantOwner(userId, token);
        UserDTO userDTO = UserDTO.fromUser(user);

        return ResponseUtil.success("Restaurant owner approved successfully", userDTO);
    }

    @PostMapping("/activate-user/{userId}")
    @Operation(
            summary = "Activate user",
            description = "Activates a deactivated user account"
    )
    public ResponseEntity<ApiResponse<UserDTO>> activateUser(
            @PathVariable String userId,
            @RequestHeader("Authorization") String authHeader) {

        // Uses the consolidated method from TokenUtil
        tokenUtil.validateAdminAccess(authHeader);

        String token = tokenUtil.extractToken(authHeader);
        User user = userService.reactivateAccount(userId, token);
        UserDTO userDTO = UserDTO.fromUser(user);

        return ResponseUtil.success("User activated successfully", userDTO);
    }

    @PostMapping("/deactivate-user/{userId}")
    @Operation(
            summary = "Deactivate user",
            description = "Deactivates a user account"
    )
    public ResponseEntity<ApiResponse<UserDTO>> deactivateUser(
            @PathVariable String userId,
            @RequestHeader("Authorization") String authHeader) {

        // Uses the consolidated method from TokenUtil
        tokenUtil.validateAdminAccess(authHeader);

        String token = tokenUtil.extractToken(authHeader);
        User user = userService.deactivateAccountByAdmin(userId, token);
        UserDTO userDTO = UserDTO.fromUser(user);

        return ResponseUtil.success("User deactivated successfully", userDTO);
    }

    @DeleteMapping("/users/{userId}")
    @Operation(
            summary = "Delete user",
            description = "Permanently deletes a user account"
    )
    public ResponseEntity<ApiResponse<Void>> deleteUserPermanently(
            @PathVariable String userId,
            @RequestHeader("Authorization") String authHeader) {

        // Uses the consolidated method from TokenUtil
        tokenUtil.validateAdminAccess(authHeader);

        // Extract token
        String token = tokenUtil.extractToken(authHeader);

        // Perform the permanent deletion
        userService.deleteUserPermanentlyByAdmin(userId, token);

        return ResponseUtil.success("User permanently deleted");
    }

    @GetMapping("/dashboard/stats")
    @Operation(
            summary = "Get dashboard statistics",
            description = "Retrieves key stats for the admin dashboard"
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats(
            @RequestHeader("Authorization") String authHeader) {

        // Uses the consolidated method from TokenUtil
        tokenUtil.validateAdminAccess(authHeader);

        Map<String, Object> stats = new HashMap<>();

        // Get counts of different user types
        Long totalUsers = userService.countAllUsers();
        Long totalCustomers = userService.countUsersByRole(Role.ROLE_CUSTOMER);
        Long totalRestaurantOwners = userService.countUsersByRole(Role.ROLE_RESTAURANT_OWNER);
        Long pendingApprovals = userService.countPendingRestaurantOwners();

        stats.put("totalUsers", totalUsers);
        stats.put("totalCustomers", totalCustomers);
        stats.put("totalRestaurantOwners", totalRestaurantOwners);
        stats.put("pendingApprovals", pendingApprovals);
        stats.put("newUsersThisWeek", userService.countNewUsersInLastDays(7));
        stats.put("activeUsers", userService.countActiveUsers());

        return ResponseUtil.success("Dashboard stats retrieved successfully", stats);
    }
}
