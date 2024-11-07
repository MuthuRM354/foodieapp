package com.foodieapp.notification.repository;

import com.foodieapp.notification.model.Notification;
import com.foodieapp.notification.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(String userId);
    List<Notification> findByUserIdAndIsRead(String userId, Boolean isRead);
    List<Notification> findByUserIdAndType(String userId, NotificationType type);
    List<Notification> findByUserIdAndTimestampBetween(String userId, LocalDateTime start, LocalDateTime end);
    List<Notification> findByType(NotificationType type);
}
