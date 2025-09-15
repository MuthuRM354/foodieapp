package com.foodieapp.user.service;

import com.foodieapp.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class IdGeneratorService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");
    private final UserRepository userRepository;

    public IdGeneratorService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Generate a customer ID with format CUSTYYMM0001
     */
    public String generateCustomerId() {
        String prefix = "CUST" + getCurrentYearMonth();
        return generateId(prefix, 4);
    }

    /**
     * Generate a restaurant owner ID with format ROYYMM0001
     */
    public String generateRestaurantOwnerId() {
        String prefix = "RO" + getCurrentYearMonth();
        return generateId(prefix, 3);
    }

    /**
     * Generate an admin ID with format ADMIN0001
     */
    public String generateAdminId() {
        return generateId("ADMIN", 4);
    }

    /**
     * Generate a restaurant ID with format RESTYYMM0001
     */
    public String generateRestaurantId() {
        String prefix = "REST" + getCurrentYearMonth();
        return generateId(prefix, 4);
    }

    /**
     * Get the current year and month in YYYYMM format
     */
    private String getCurrentYearMonth() {
        return LocalDateTime.now().format(MONTH_FORMATTER);
    }

    /**
     * Generate a new ID with the given prefix and sequence number length
     */
    private String generateId(String prefix, int sequenceLength) {
        // Find the maximum existing ID with this prefix
        String maxId = userRepository.findMaxIdByPrefix(prefix);

        int nextSequence = 1; // Default starting sequence

        // If we found an existing ID with this prefix, extract and increment the sequence
        if (maxId != null && !maxId.isEmpty()) {
            String sequencePart = maxId.substring(prefix.length());
            try {
                nextSequence = Integer.parseInt(sequencePart) + 1;
            } catch (NumberFormatException e) {
                // If parsing fails, start from 1
                nextSequence = 1;
            }
        }

        // Format the sequence number with leading zeros
        String sequenceFormat = "%0" + sequenceLength + "d";
        String formattedSequence = String.format(sequenceFormat, nextSequence);

        return prefix + formattedSequence;
    }
}