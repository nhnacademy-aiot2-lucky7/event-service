package com.nhnacademy.notification;

import com.nhnacademy.event.dto.EventSourceResponse;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
public class NotificationResponse {
    private Long notificationNo;

    private String eventDetails;

    private String eventLevel;

    private LocalDateTime eventAt;

    private String departmentId;

    private EventSourceResponse eventSource;
}
