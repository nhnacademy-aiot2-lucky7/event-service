package com.nhnacademy.notification.service;

import com.nhnacademy.event.domain.Event;
import com.nhnacademy.event.dto.EventResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    void saveNotification(Event event);

    void deleteReadNotifications();

    Long countUnreadNotifications();

    Page<EventResponse> findNotificationsByReadStatus(boolean isRead, Pageable pageable);
}
