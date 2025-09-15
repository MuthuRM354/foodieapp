package com.foodieapp.payment.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class IdGeneratorService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Random RANDOM = new Random();
    private final AtomicInteger sequence = new AtomicInteger(1);

    /**
     * Generates a unique payment ID with format: DATE_PREFIX-RANDOM-SEQUENCE
     */
    public String generatePaymentId() {
        String datePrefix = LocalDateTime.now().format(DATE_FORMATTER);
        int randomPart = RANDOM.nextInt(1000);
        int sequenceNumber = sequence.getAndIncrement();

        // Fixed: Removed redundant assignment where a variable was assigned to itself
        // Return the generated payment ID directly
        return String.format("PAY-%s-%03d-%04d", datePrefix, randomPart, sequenceNumber);
    }

    /**
     * Generates a unique transaction reference
     */
    public String generateTransactionReference() {
        String datePrefix = LocalDateTime.now().format(DATE_FORMATTER);
        int randomPart = RANDOM.nextInt(10000);

        return String.format("TXN-%s-%04d", datePrefix, randomPart);
    }
}
