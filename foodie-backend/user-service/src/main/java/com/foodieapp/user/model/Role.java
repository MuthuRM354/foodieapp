package com.foodieapp.user.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "roles")
public class Role {
    // Role constants to replace USER_ROLE enum
    public static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_RESTAURANT_OWNER = "ROLE_RESTAURANT_OWNER";

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Utility methods
    public boolean isCustomer() {
        return ROLE_CUSTOMER.equals(name);
    }

    public boolean isAdmin() {
        return ROLE_ADMIN.equals(name);
    }

    public boolean isRestaurantOwner() {
        return ROLE_RESTAURANT_OWNER.equals(name);
    }
}
