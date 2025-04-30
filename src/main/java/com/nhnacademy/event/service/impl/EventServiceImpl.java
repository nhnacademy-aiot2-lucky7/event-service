package com.nhnacademy.event.service.impl;

import com.nhnacademy.event.domain.Event;
import com.nhnacademy.event.dto.EventCreateRequest;
import com.nhnacademy.event.dto.EventResponse;
import com.nhnacademy.event.repository.EventRepository;
import com.nhnacademy.event.service.EventService;
import com.nhnacademy.eventsource.domain.EventSource;
import com.nhnacademy.eventsource.repository.EventSourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventSourceRepository eventSourceRepository;

    public void createEvent(EventCreateRequest eventCreateRequest) {
        EventSource eventSource = EventSource.builder()
                .sourceId(eventCreateRequest.getSourceId())
                .sourceType(eventCreateRequest.getSourceType())
                .build();

        // sourceId가 없다면 event_source 테이블에 저장. 있다면 이벤트만 저장.
        if (!eventSourceRepository.existsById(eventCreateRequest.getSourceId())) {
            eventSourceRepository.save(eventSource);
        }

        Event event = Event.builder()
                .eventDetails(eventCreateRequest.getEventDetails())
                .eventLevel(eventCreateRequest.getEventLevel())
                .eventSource(eventSource)
                .departmentId(eventCreateRequest.getDepartmentId())
                .build();

        eventRepository.save(event);
    }

    public void removeEvent(Long eventNo) {
        eventRepository.deleteById(eventNo);
    }

    @Transactional(readOnly = true)
    public Page<EventResponse> findByDepartmentIdAndSourceId(String departmentId, String sourceId, Pageable pageable) {
        if (eventSourceRepository.existsById(departmentId)) {

        }

        return eventRepository.findByDepartmentIdAndSourceId(departmentId, sourceId, pageable);
    }
}
