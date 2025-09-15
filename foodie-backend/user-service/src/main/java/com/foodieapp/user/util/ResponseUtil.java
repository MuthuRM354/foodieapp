package com.foodieapp.user.util;

import com.foodieapp.user.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Utility class for creating consistent API responses
 */
public class ResponseUtil {

    /**
     * Create a success response with data
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        return ResponseEntity.ok(ApiResponse.success(message, data));
    }

    /**
     * Create a success response without data
     */
    public static ResponseEntity<ApiResponse<Void>> success(String message) {
        return ResponseEntity.ok(ApiResponse.success(message, null));
    }

    /**
     * Create a created response with data
     */
    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(message, data));
    }

    /**
     * Create a created response without data
     */
    public static ResponseEntity<ApiResponse<Void>> created(String message) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(message, null));
    }

    /**
     * Create an error response with bad request status
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String message) {
        return ResponseEntity.badRequest().body(ApiResponse.error(message));
    }

    /**
     * Create an error response with specific status
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(ApiResponse.error(message));
    }

    /**
     * Create an error response with bad request status and error code
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, String errorCode) {
        return ResponseEntity.badRequest().body(ApiResponse.error(message, errorCode));
    }

    /**
     * Create an error response with specific status and error code
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, String errorCode, HttpStatus status) {
        return ResponseEntity.status(status).body(ApiResponse.error(message, errorCode));
    }

    /**
     * Create an error response with specific status, error code and additional data
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, String errorCode, T data, HttpStatus status) {
        return ResponseEntity.status(status).body(ApiResponse.error(message, errorCode, data));
    }
}