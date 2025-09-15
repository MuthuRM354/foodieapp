package com.foodieapp.notification.client;

import com.foodieapp.notification.exception.NotificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Base class for all service clients that provides standard functionality
 * for REST calls, caching, error handling, and authorization.
 */
public abstract class AbstractServiceClient {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final RestTemplate restTemplate;
    protected final String serviceUrl;

    // Cache mechanism
    private final Map<String, CacheEntry<Object>> responseCache = new ConcurrentHashMap<>();
    protected static final long DEFAULT_CACHE_EXPIRATION_MS = TimeUnit.MINUTES.toMillis(5);

    protected AbstractServiceClient(RestTemplate restTemplate, String serviceUrl) {
        this.restTemplate = restTemplate;
        this.serviceUrl = serviceUrl;
    }

    /**
     * Create standard HTTP headers with authorization
     */
    protected HttpHeaders createHeaders(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        if (authToken != null && !authToken.isEmpty()) {
            // Ensure token has the "Bearer " prefix
            if (!authToken.startsWith("Bearer ")) {
                authToken = "Bearer " + authToken;
            }
            headers.set("Authorization", authToken);
        }

        return headers;
    }

    /**
     * Create HttpEntity with body and headers
     */
    protected <T> HttpEntity<T> createEntity(T body, String authToken) {
        return new HttpEntity<>(body, createHeaders(authToken));
    }

    /**
     * Create HttpEntity with just headers
     */
    protected HttpEntity<?> createEntity(String authToken) {
        return new HttpEntity<>(createHeaders(authToken));
    }

    /**
     * Execute HTTP GET request
     */
    protected <T> T getForObject(String path, Class<T> responseType, String authToken) {
        try {
            String url = serviceUrl + path;
            HttpEntity<?> entity = createEntity(authToken);

            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    responseType);

            return response.getBody();
        } catch (Exception e) {
            return handleApiError(e, "GET " + path);
        }
    }

    /**
     * Execute HTTP GET request without auth token
     */
    protected <T> T getForObject(String path, Class<T> responseType) {
        return getForObject(path, responseType, null);
    }

    /**
     * Execute HTTP POST request
     */
    protected <T, R> R postForObject(String path, T body, Class<R> responseType, String authToken) {
        try {
            String url = serviceUrl + path;
            HttpEntity<T> entity = createEntity(body, authToken);

            return restTemplate.postForObject(url, entity, responseType);
        } catch (Exception e) {
            return handleApiError(e, "POST " + path);
        }
    }

    /**
     * Execute a request with any HTTP method and authorization
     * @param path The API path
     * @param method The HTTP method to use
     * @param body The request body (can be null for GET, DELETE, etc.)
     * @param responseType The expected response type
     * @param authToken The authorization token
     * @return ResponseEntity containing the response, empty if there was an error
     */
    protected <T, R> ResponseEntity<R> exchangeWithAuth(
            String path,
            HttpMethod method,
            T body,
            Class<R> responseType,
            String authToken) {
        try {
            String url = serviceUrl + path;
            HttpEntity<T> entity = body != null
                ? createEntity(body, authToken)
                : (HttpEntity<T>) createEntity(authToken);

            return restTemplate.exchange(
                url,
                method,
                entity,
                responseType);
        } catch (Exception e) {
            // Log the error
            handleApiError(e, method + " " + path);
            // Throw an exception instead of returning null
            throw new NotificationException("Failed to execute " + method + " " + path + ": " + e.getMessage(), e);
        }
    }

    /**
     * Standard error handling for API calls
     */
    protected <T> T handleApiError(Exception e, String operation) {
        if (e instanceof ResourceAccessException) {
            logger.error("Service unavailable during {}: {}", operation, e.getMessage());
        } else if (e instanceof RestClientException) {
            logger.error("REST client error during {}: {}", operation, e.getMessage());
        } else {
            logger.error("Unexpected error during {}: {}", operation, e.getMessage());
        }
        throw new NotificationException("Error during " + operation + ": " + e.getMessage(), e);
    }

    /**
     * Get cached value or compute if absent or expired
     */
    protected <T> T getCachedOrCompute(String cacheKey, Supplier<T> supplier, long expirationMs) {
        CacheEntry<Object> entry = responseCache.get(cacheKey);

        if (entry != null && !entry.isExpired()) {
            logger.debug("Using cached response for key: {}", cacheKey);
            return (T) entry.getValue();
        }

        T result = supplier.get();
        if (result != null) {
            responseCache.put(cacheKey, new CacheEntry<>(result, expirationMs));
        }
        return result;
    }

    /**
     * Get cached value or compute with default expiration
     */
    protected <T> T getCachedOrCompute(String cacheKey, Supplier<T> supplier) {
        return getCachedOrCompute(cacheKey, supplier, DEFAULT_CACHE_EXPIRATION_MS);
    }

    /**
     * Cache entry class
     */
    protected static class CacheEntry<T> {
        private final T value;
        private final long expirationTime;

        public CacheEntry(T value, long expirationMs) {
            this.value = value;
            this.expirationTime = System.currentTimeMillis() + expirationMs;
        }

        public T getValue() {
            return value;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
}
