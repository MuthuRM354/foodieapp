package com.foodieapp.notification.repository;

import com.foodieapp.notification.entity.NotificationLog;
import com.foodieapp.notification.util.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationLogRepository extends MongoRepository<NotificationLog, String> {
    // Add these methods
    Page<NotificationLog> findByRecipientAndType(String recipient, NotificationType type, Pageable pageable);
    Page<NotificationLog> findByRecipient(String recipient, Pageable pageable);
    Page<NotificationLog> findByType(NotificationType type, Pageable pageable);
}