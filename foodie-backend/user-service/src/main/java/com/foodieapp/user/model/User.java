package com.foodieapp.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foodieapp.user.util.ValidationConstants;
import com.foodieapp.user.util.ValidationConstants.ValidPhone;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;

    @Email(message = ValidationConstants.INVALID_EMAIL_MESSAGE)
    @Column(unique = true, nullable = false)
    private String email;

    @Size(min = ValidationConstants.FULLNAME_MIN_LENGTH, max = ValidationConstants.FULLNAME_MAX_LENGTH)
    @Column(nullable = false)
    private String fullName;

    @Column(unique = true)
    @Size(min = ValidationConstants.USERNAME_MIN_LENGTH, max = ValidationConstants.USERNAME_MAX_LENGTH,
            message = ValidationConstants.USERNAME_LENGTH_MESSAGE)
    private String username;

    @ValidPhone
    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @JsonIgnore // Don't serialize password
    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Column(name = "email_verified")
    private boolean emailVerified = false;

    @Column(name = "phone_verified")
    private boolean phoneVerified = false;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "social_provider")
    private String socialProvider;

    @Column(name = "social_id")
    private String socialId;

    @Column(name = "enabled")
    private boolean enabled = true;

    @Column(name = "account_locked")
    private boolean accountLocked = false;

    @Column(name = "failed_login_attempts")
    private int failedLoginAttempts = 0;

    @Column(name = "last_login_attempt")
    private LocalDateTime lastLoginAttempt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Default constructor
    public User() {
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Updated role handling methods
    public String getRole() {
        if (roles == null || roles.isEmpty()) {
            return Role.ROLE_CUSTOMER;
        }
        return roles.iterator().next().getName();
    }

    // Set multiple roles
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    // Add a role
    public void addRole(String roleName) {
        Role newRole = new Role(roleName);
        this.roles.add(newRole);
    }

    // Remove a role
    public void removeRole(String roleName) {
        this.roles.removeIf(r -> r.getName().equals(roleName));
    }

    /**
     * Check if user has a specific role
     */
    public boolean hasRole(String roleToCheck) {
        return roles.stream()
                .map(Role::getName)
                .anyMatch(name -> name.equals(roleToCheck));
    }

    // Get all roles as strings
    public Set<String> getRoleNames() {
        if (roles == null || roles.isEmpty()) {
            return Set.of(Role.ROLE_CUSTOMER);
        }

        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getSocialProvider() {
        return socialProvider;
    }

    public void setSocialProvider(String socialProvider) {
        this.socialProvider = socialProvider;
    }

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public LocalDateTime getLastLoginAttempt() {
        return lastLoginAttempt;
    }

    public void setLastLoginAttempt(LocalDateTime lastLoginAttempt) {
        this.lastLoginAttempt = lastLoginAttempt;
    }

    // Method to record failed login
    public void recordFailedLogin() {
        this.failedLoginAttempts++;
        this.lastLoginAttempt = LocalDateTime.now();

        // Lock account after 5 failed attempts
        if (this.failedLoginAttempts >= 5) {
            this.accountLocked = true;
        }
    }

    // Method to reset failed login counter
    public void resetFailedLogins() {
        this.failedLoginAttempts = 0;
        this.accountLocked = false;
    }
}
