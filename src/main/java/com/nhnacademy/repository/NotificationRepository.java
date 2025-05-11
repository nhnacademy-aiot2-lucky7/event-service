package com.nhnacademy.repository;

import com.nhnacademy.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long>, CustomNotificationRepository {
    Long countByUserNoAndIsReadFalse(Long userNo);

    void deleteByUserNoAndIsReadTrue(Long userNo);
}
