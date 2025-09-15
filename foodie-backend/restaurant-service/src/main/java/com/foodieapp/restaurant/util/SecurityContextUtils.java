package com.foodieapp.restaurant.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;

public class SecurityContextUtils {

    private SecurityContextUtils() {
        // Utility class, no instantiation
    }

    /**
     * Get current user ID from the security context
     */
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getPrincipal().toString();
        }
        return null;
    }

    /**
     * Check if current user is an admin
     */
    public static boolean isCurrentUserAdmin() {
        return hasRole("ROLE_ADMIN");
    }

    /**
     * Check if current user is a restaurant owner
     */
    public static boolean isCurrentUserRestaurantOwner() {
        return hasRole("ROLE_RESTAURANT_OWNER");
    }

    /**
     * Check if user has a specific role
     */
    private static boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (authorities != null) {
                for (GrantedAuthority authority : authorities) {
                    if (roleName.equals(authority.getAuthority())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Get the authorization header from the current request
     */
    public static String getCurrentAuthHeader() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getHeader("Authorization");
            }
        } catch (Exception e) {
            // Log error but don't propagate
        }
        return null;
    }
}
