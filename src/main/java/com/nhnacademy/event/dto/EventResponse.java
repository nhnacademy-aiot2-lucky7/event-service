package com.nhnacademy.event.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class EventResponse {
    private String eventDetails;

    private String eventLevel;

    private LocalDateTime eventAt;

    private String sourceId;
}