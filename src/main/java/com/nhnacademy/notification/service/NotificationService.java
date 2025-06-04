package com.nhnacademy.notification.service;

import com.nhnacademy.event.domain.Event;
import com.nhnacademy.event.dto.EventResponse;
import com.nhnacademy.notification.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    EventResponse getNotification(Long notificationNo);
    
    void saveNotification(Event event);

    void deleteReadNotifications();

    Long countUnreadNotifications();

    Page<NotificationResponse> findNotificationsByReadStatus(boolean isRead, Pageable pageable);
}
