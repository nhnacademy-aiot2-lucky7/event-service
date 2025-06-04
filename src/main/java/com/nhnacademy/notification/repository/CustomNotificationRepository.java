package com.nhnacademy.notification.repository;

import com.nhnacademy.event.dto.EventResponse;
import com.nhnacademy.notification.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomNotificationRepository {
    Page<NotificationResponse> findNotifications(Long userNo, Boolean isRead, Pageable pageable);
}
