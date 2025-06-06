package com.nhnacademy.notification.controller;

import com.nhnacademy.event.dto.EventResponse;
import com.nhnacademy.notification.NotificationResponse;
import com.nhnacademy.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/{notification-no}")
    public ResponseEntity<EventResponse> getNotification(@PathVariable("notification-no") Long notificationNo) {
        return ResponseEntity.ok(notificationService.getNotification(notificationNo));
    }

    // 알림 목록 조회 (읽음 여부 필터링)
    @GetMapping("/read")
    public ResponseEntity<Page<NotificationResponse>> getNotificationsByRead(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<NotificationResponse> result = notificationService.findNotificationsByReadStatus(true, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/unread")
    public ResponseEntity<Page<NotificationResponse>> getNotificationsByUnRead(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<NotificationResponse> result = notificationService.findNotificationsByReadStatus(false, pageable);
        return ResponseEntity.ok(result);
    }

    // 안 읽은 알림 개수 조회
    @GetMapping("/unread-count")
    public ResponseEntity<Long> countUnreadNotifications() {
        Long count = notificationService.countUnreadNotifications();
        return ResponseEntity.ok(count);
    }

    // 읽은 알림 삭제
    @DeleteMapping("/read")
    public ResponseEntity<Void> deleteReadNotifications() {
        notificationService.deleteReadNotifications();
        return ResponseEntity.noContent().build();
    }
}
