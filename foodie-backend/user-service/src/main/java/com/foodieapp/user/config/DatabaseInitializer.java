package com.foodieapp.user.config;

import com.foodieapp.user.model.Role;
import com.foodieapp.user.model.User;
import com.foodieapp.user.repository.RoleRepository;
import com.foodieapp.user.repository.UserRepository;
import com.foodieapp.user.service.IdGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Configuration
public class DatabaseInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    // Default roles to initialize
    private static final List<String> DEFAULT_ROLES = List.of(
            Role.ROLE_CUSTOMER,
            Role.ROLE_ADMIN,
            Role.ROLE_RESTAURANT_OWNER
    );

    @Value("${app.admin.email:admin@foodieapp.com}")
    private String adminEmail;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:#{null}}")
    private String adminPassword;

    @Value("${app.admin.phone:+1234567890}")  // Add default phone number
    private String adminPhone;

    @Bean
    @Transactional
    public CommandLineRunner initDatabase(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            IdGeneratorService idGeneratorService) {
        return args -> {
            try {
                logger.info("Initializing database with default roles...");
                initializeRoles(roleRepository);

                // Create admin user if needed
                if (shouldCreateAdmin()) {
                    createAdminUser(roleRepository, userRepository, passwordEncoder, idGeneratorService);
                }

                logger.info("Database initialization completed successfully");
            } catch (DataAccessException e) {
                logger.error("Could not initialize database: {}", e.getMessage(), e);
            }
        };
    }

    /**
     * Initialize default roles if they don't exist
     */
    private void initializeRoles(RoleRepository roleRepository) {
        for (String roleName : DEFAULT_ROLES) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                logger.info("Creating role: {}", roleName);
                roleRepository.save(new Role(roleName));
            } else {
                logger.debug("Role already exists: {}", roleName);
            }
        }
    }

    /**
     * Check if admin user should be created
     */
    private boolean shouldCreateAdmin() {
        return adminPassword != null && !adminPassword.isEmpty();
    }

    /**
     * Create admin user if it doesn't exist
     */
    private void createAdminUser(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            IdGeneratorService idGeneratorService) {

        logger.info("Checking if initial admin user creation is needed...");

        // Only create admin if none exists with this email
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            logger.info("Creating initial admin user with email: {}", adminEmail);

            // Find admin role
            Optional<Role> adminRoleOpt = roleRepository.findByName(Role.ROLE_ADMIN);
            if (adminRoleOpt.isEmpty()) {
                logger.error("Admin role not found, cannot create admin user");
                return;
            }

            // Create admin user
            User adminUser = new User();
            adminUser.setId(idGeneratorService.generateAdminId());
            adminUser.setEmail(adminEmail);
            adminUser.setUsername(adminUsername);
            adminUser.setFullName("System Administrator");
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setPhoneNumber(adminPhone);  // Set the phone number here
            adminUser.setEmailVerified(true);
            adminUser.setEnabled(true);

            // Set admin role
            Set<Role> roles = new HashSet<>();
            roles.add(adminRoleOpt.get());
            adminUser.setRoles(roles);

            userRepository.save(adminUser);
            logger.info("Initial admin user created successfully with ID: {}", adminUser.getId());
        } else {
            logger.info("Admin user already exists, skipping creation");
        }
    }
}
