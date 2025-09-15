package com.foodieapp.user.security;

import com.foodieapp.user.exception.TokenExpiredException;
import com.foodieapp.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    // Default development JWT secret - only used if env variable is not set
    // DO NOT USE THIS IN PRODUCTION
    private static final String DEV_SECRET = "devSecretKey1234567890abcdefghijklmnopqrstuvwxyz";

    @Value("${app.jwt.secret:}")
    private String secretKey;

    @Value("${app.jwt.expiration:86400000}") // Default: 24 hours in milliseconds
    private long jwtExpiration;

    @Value("${app.jwt.refresh-expiration:604800000}") // Default: 7 days in milliseconds
    private long refreshExpiration;

    @Value("${app.jwt.verification-expiration:86400000}") // Default: 24 hours in milliseconds
    private long verificationExpiration;

    private final Environment environment;

    private Key signingKey;

    public JwtService(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init() {
        // If secretKey is empty, use development secret in non-production environments
        if (secretKey == null || secretKey.isEmpty()) {
            // Check if we're in dev/test environment
            String[] activeProfiles = environment.getActiveProfiles();
            boolean isProduction = Arrays.stream(activeProfiles).anyMatch(p -> p.equals("prod") || p.equals("production"));

            if (isProduction) {
                logger.error("JWT secret key is not configured. The application will not function correctly.");
                logger.error("Set the JWT_SECRET environment variable before running in production.");
            } else {
                logger.warn("Using default development JWT secret. DO NOT USE IN PRODUCTION!");
                secretKey = DEV_SECRET;
            }
        }

        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        logger.info("JWT Service initialized with signing key");
    }

    // Extract username from token
    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (ExpiredJwtException e) {
            logger.warn("Token expired while extracting username");
            throw new TokenExpiredException("JWT token has expired");
        } catch (Exception e) {
            logger.error("Error extracting username from token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token");
        }
    }

    // Extract any claim from token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Primary token generation method for all scenarios
    public String generateToken(Object subject, Map<String, Object> claims, long expiration) {
        String subjectString;

        // Handle different subject types
        if (subject instanceof User) {
            User user = (User) subject;
            subjectString = user.getEmail();

            // Add user-specific claims if not already present
            if (!claims.containsKey("userId")) claims.put("userId", user.getId());
            if (!claims.containsKey("email")) claims.put("email", user.getEmail());
            if (!claims.containsKey("roles")) claims.put("roles", user.getRoleNames());
        } else if (subject instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) subject;
            subjectString = userDetails.getUsername();

            // Add authorities claim for UserDetails
            if (!claims.containsKey("authorities")) {
                claims.put("authorities", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")));
            }
        } else if (subject instanceof String) {
            subjectString = (String) subject;
        } else {
            throw new IllegalArgumentException("Subject must be User, UserDetails, or String");
        }

        // Add standard claims
        if (!claims.containsKey("timestamp")) claims.put("timestamp", System.currentTimeMillis());
        if (!claims.containsKey("tokenId")) claims.put("tokenId", java.util.UUID.randomUUID().toString());

        return buildToken(claims, subjectString, expiration);
    }

    // Simplified access token generation for User
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("roles", user.getRoleNames());
        claims.put("timestamp", System.currentTimeMillis());
        claims.put("tokenId", java.util.UUID.randomUUID().toString());
        return generateToken(user, claims, jwtExpiration);
    }

    // Simplified refresh token generation for User
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenType", "refresh");
        return generateToken(user, claims, refreshExpiration);
    }

    // Simplified verification token generation
    public String generateVerificationToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenType", "verification");
        return generateToken(user, claims, verificationExpiration);
    }

    // Get refresh token expiration for external use
    public long getRefreshExpiration() {
        return refreshExpiration;
    }

    // Build token with claims, subject, and expiration
    private String buildToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate token without UserDetails
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("Token expired during validation");
            return false;
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    // Validate token with UserDetails
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            logger.error("Token expired: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    // Check if token is expired
    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    // Extract expiration date from token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract all claims from token
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // Return the claims even if the token is expired
            // This is useful for getting info from expired tokens
            logger.warn("Token expired but still extracting claims");
            return e.getClaims();
        }
    }

    // Get token expiration date
    public Date getExpirationDateFromToken(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Get issue date from token
    public Date getIssuedAtDateFromToken(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }
}
