package com.nhnacademy.event.dto;

import com.nhnacademy.event.domain.Event;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
public class EventResponse {
    private Long eventNo;

    private String eventDetails;

    private String eventLevel;

    private LocalDateTime eventAt;

    private String departmentId;

    private EventSourceResponse eventSource;

    public static EventResponse from(Event event) {
        return new EventResponse(
                event.getEventNo(),
                event.getEventDetails(),
                event.getLevelName(),
                event.getEventAt(),
                event.getDepartmentId(),
                new EventSourceResponse(
                        event.getEventSource().getSourceId(),
                        event.getEventSource().getSourceType()
                )
        );
    }
}