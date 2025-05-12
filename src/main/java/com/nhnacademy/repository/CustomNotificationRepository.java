package com.nhnacademy.repository;

import com.nhnacademy.event.dto.EventResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomNotificationRepository {
    Page<EventResponse> findNotifications(Long userNo, Boolean isRead, Pageable pageable);
}
