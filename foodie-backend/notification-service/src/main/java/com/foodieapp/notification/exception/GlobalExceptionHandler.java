package com.foodieapp.notification.exception;

import com.foodieapp.notification.dto.NotificationResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<NotificationResponseDTO> handleNotificationException(NotificationException ex) {
        NotificationResponseDTO response = NotificationResponseDTO.builder()
                .status("ERROR")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<NotificationResponseDTO> handleGenericException(Exception ex) {
        NotificationResponseDTO response = NotificationResponseDTO.builder()
                .status("ERROR")
                .message("An unexpected error occurred: " + ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}