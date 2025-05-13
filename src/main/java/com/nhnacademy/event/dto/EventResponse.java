package com.nhnacademy.event.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
public class EventResponse {
    private String eventDetails;

    private String eventLevel;

    private LocalDateTime eventAt;

    private String departmentId;

    private EventSourceResponse eventSource;
}