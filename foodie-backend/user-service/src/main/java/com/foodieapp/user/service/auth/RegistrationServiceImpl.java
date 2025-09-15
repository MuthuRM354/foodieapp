package com.foodieapp.user.service.auth;

import com.foodieapp.user.dto.request.CreateUserRequest;
import com.foodieapp.user.dto.request.OtpRequest;
import com.foodieapp.user.dto.response.ApiResponse.AuthResponse;
import com.foodieapp.user.exception.ResourceNotFoundException;
import com.foodieapp.user.exception.UnauthorizedException;
import com.foodieapp.user.exception.UserAlreadyExistsException;
import com.foodieapp.user.model.Role;
import com.foodieapp.user.model.User;
import com.foodieapp.user.repository.RoleRepository;
import com.foodieapp.user.repository.UserRepository;
import com.foodieapp.user.security.JwtService;
import com.foodieapp.user.service.IdGeneratorService;
import com.foodieapp.user.service.email.EmailService;
import com.foodieapp.user.service.verification.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final IdGeneratorService idGeneratorService;
    private final EmailService emailService;

    public RegistrationServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            OtpService otpService,
            IdGeneratorService idGeneratorService,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
        this.idGeneratorService = idGeneratorService;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public AuthResponse registerCustomer(CreateUserRequest request) {
        // Validate uniqueness
        validateUserUniqueness(request);

        // Get customer role
        Role userRole = roleRepository.findByName(Role.ROLE_CUSTOMER)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        // Create user with customer ID and set enabled to false until validated
        User user = createUser(request, Set.of(userRole));
        user.setId(idGeneratorService.generateCustomerId());
        user.setEnabled(false); // Disable until validated
        User savedUser = userRepository.save(user);

        // Generate verification token
        String verificationToken = jwtService.generateVerificationToken(savedUser);

        // Send verification based on method
        if ("email".equals(request.getVerificationMethod())) {
            // Send email verification link only (no OTP)
            emailService.sendVerificationEmail(request.getEmail(), verificationToken, savedUser.getId());
            logger.info("Verification email sent to: {}", request.getEmail());
        } else if ("phone".equals(request.getVerificationMethod())) {
            // For phone, still use OTP
            String otp = otpService.generateOtp(request.getPhoneNumber());
            otpService.sendSmsOtp(request.getPhoneNumber(), otp);
            logger.info("Phone verification OTP sent to: {}", request.getPhoneNumber());
        }

        // Generate tokens for response
        return generateAuthResponse(savedUser, verificationToken);
    }

    @Override
    @Transactional
    public AuthResponse registerRestaurantOwner(CreateUserRequest request) {
        // Validate uniqueness
        validateUserUniqueness(request);

        // Get restaurant owner role
        Role ownerRole = roleRepository.findByName(Role.ROLE_RESTAURANT_OWNER)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        // Create user with restaurant owner ID
        User user = createUser(request, Set.of(ownerRole));
        user.setId(idGeneratorService.generateRestaurantOwnerId());
        user.setEnabled(false); // Disable until validated
        User savedUser = userRepository.save(user);

        // Generate verification token
        String verificationToken = jwtService.generateVerificationToken(savedUser);

        // Send verification based on method
        if ("email".equals(request.getVerificationMethod())) {
            // Send email verification link only (no OTP)
            emailService.sendVerificationEmail(request.getEmail(), verificationToken, savedUser.getId());
            logger.info("Verification email sent to: {}", request.getEmail());
        } else if ("phone".equals(request.getVerificationMethod())) {
            // For phone, still use OTP
            String otp = otpService.generateOtp(request.getPhoneNumber());
            otpService.sendSmsOtp(request.getPhoneNumber(), otp);
            logger.info("Phone verification OTP sent to: {}", request.getPhoneNumber());
        }

        // Generate tokens for response
        return generateAuthResponse(savedUser, verificationToken);
    }

    @Override
    @Transactional
    public AuthResponse registerAdmin(CreateUserRequest request) {
        // Validate uniqueness
        validateUserUniqueness(request);

        // Get admin role
        Role adminRole = roleRepository.findByName(Role.ROLE_ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        // Create user with admin ID
        User user = createUser(request, Set.of(adminRole));
        user.setId(idGeneratorService.generateAdminId());
        user.setEnabled(false); // Disable until validated
        User savedUser = userRepository.save(user);

        // Generate verification token
        String verificationToken = jwtService.generateVerificationToken(savedUser);

        // Send verification based on method
        if ("email".equals(request.getVerificationMethod())) {
            // Send email verification link only (no OTP)
            emailService.sendVerificationEmail(request.getEmail(), verificationToken, savedUser.getId());
            logger.info("Verification email sent to: {}", request.getEmail());
        } else if ("phone".equals(request.getVerificationMethod())) {
            // For phone, still use OTP
            String otp = otpService.generateOtp(request.getPhoneNumber());
            otpService.sendSmsOtp(request.getPhoneNumber(), otp);
            logger.info("Phone verification OTP sent to: {}", request.getPhoneNumber());
        }

        // Generate tokens for response
        return generateAuthResponse(savedUser, verificationToken);
    }

    @Override
    @Transactional
    public AuthResponse verifyRegistrationOtp(OtpRequest request) {
        String userId = request.getUserId();
        String otp = request.getOtp();
        String verificationMethod = request.getType();

        // Find the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Verify OTP (only for phone verification)
        if ("phone".equals(verificationMethod)) {
            boolean isValid = verifyAndUpdatePhone(user, otp);
            if (!isValid) {
                throw new UnauthorizedException("Invalid OTP");
            }

            // Save the updated user
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Invalid verification method. Email verification should use the verification link.");
        }

        // Generate new tokens
        return generateAuthResponse(user);
    }

    // Helper method to validate user uniqueness
    private void validateUserUniqueness(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already in use");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new UserAlreadyExistsException("Phone number already in use");
        }
    }

    // Helper method to create a new user
    private User createUser(CreateUserRequest request, Set<Role> roles) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRoles(roles);
        return user;
    }

    // Helper method to verify and update phone
    private boolean verifyAndUpdatePhone(User user, String otp) {
        boolean isValid = otpService.validateOtp(user.getPhoneNumber(), otp);
        if (isValid) {
            user.setPhoneVerified(true);
            user.setEnabled(true); // Enable account after phone verification
            otpService.clearOtp(user.getPhoneNumber());
        }
        return isValid;
    }

    // Helper method to generate auth response
    private AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole()
        );
    }

    // Helper method to generate auth response with verification token
    private AuthResponse generateAuthResponse(User user, String verificationToken) {
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                verificationToken
        );
    }
}
