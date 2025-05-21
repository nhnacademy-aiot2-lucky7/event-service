package com.nhnacademy.event.elasticsearch.document;

import com.nhnacademy.event.domain.Event;
import lombok.*;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDocument {
    private Long eventNo;
    private String eventDetails;
    private String levelName;
    private String eventAt;
    private String departmentId;
    private EventSourceDocument eventSource;

    public static EventDocument from(Event event) {
        return EventDocument.builder()
                .eventNo(event.getEventNo())
                .eventDetails(event.getEventDetails())
                .levelName(event.getLevelName())
                .eventAt(event.getEventAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .departmentId(event.getDepartmentId())
                .eventSource(new EventSourceDocument(event.getEventSource().getSourceId(), event.getEventSource().getSourceType())) // EventSource 포함
                .build();
    }
}
