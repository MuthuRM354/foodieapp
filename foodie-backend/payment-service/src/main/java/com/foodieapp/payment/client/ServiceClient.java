package com.foodieapp.payment.client;

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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Base class for all service clients that provides standard functionality
 * for REST calls, caching, error handling, and authorization.
 */
public abstract class ServiceClient {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final RestTemplate restTemplate;
    protected final String baseUrl;

    // Cache mechanism
    private final Map<String, CacheEntry<Object>> responseCache = new ConcurrentHashMap<>();
    protected static final long DEFAULT_CACHE_EXPIRATION_MS = TimeUnit.MINUTES.toMillis(5);

    public ServiceClient(RestTemplate restTemplate, String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        logger.debug("Initialized service client with base URL: {}", baseUrl);
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
     * Execute general exchange request
     */
    protected <T> ResponseEntity<T> exchange(String path, HttpMethod method, Object body, Class<T> responseType, String token) {
        try {
            HttpEntity<?> requestEntity = createEntity(body, token);
            logger.debug("Executing {} request to {}", method, baseUrl + path);
            return restTemplate.exchange(baseUrl + path, method, requestEntity, responseType);
        } catch (Exception e) {
            logger.error("Error during {} request to {}: {}", method, path, e.getMessage());
            throw handleAndReThrowException("exchange " + method + " " + path, e);
        }
    }

    /**
     * Execute exchange without token
     */
    protected <T> ResponseEntity<T> exchange(String path, HttpMethod method, Object body, Class<T> responseType) {
        return exchange(path, method, body, responseType, null);
    }

    /**
     * Execute HTTP GET request
     */
    protected <T> T getForObject(String path, Class<T> responseType, String authToken) {
        try {
            logger.debug("Executing GET request to {}", baseUrl + path);
            ResponseEntity<T> response = exchange(path, HttpMethod.GET, null, responseType, authToken);
            return response.getBody();
        } catch (Exception e) {
            return handleApiCallException("GET " + path, e, null);
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
            HttpEntity<T> entity = createEntity(body, authToken);
            logger.debug("Executing POST request to {}", baseUrl + path);
            return restTemplate.postForObject(baseUrl + path, entity, responseType);
        } catch (Exception e) {
            return handleApiCallException("POST " + path, e, null);
        }
    }

    /**
     * Standard error handling for API calls
     */
    protected <T> T handleApiCallException(String operation, Exception e, T defaultValue) {
        if (e instanceof ResourceAccessException) {
            logger.error("Service unavailable during {}: {}", operation, e.getMessage());
        } else if (e instanceof RestClientException) {
            logger.error("REST client error during {}: {}", operation, e.getMessage());
        } else {
            logger.error("Unexpected error during {}: {}", operation, e.getMessage());
        }
        return defaultValue;
    }

    /**
     * Handle exception and rethrow - for cases where we want the calling code to handle the exception
     */
    protected RuntimeException handleAndReThrowException(String operation, Exception e) {
        if (e instanceof RuntimeException) {
            handleApiCallException(operation, e, null);
            return (RuntimeException) e;
        } else {
            RuntimeException wrappedException = new RuntimeException("Error during " + operation + ": " + e.getMessage(), e);
            handleApiCallException(operation, e, null);
            return wrappedException;
        }
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
        responseCache.put(cacheKey, new CacheEntry<>(result, expirationMs));
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
