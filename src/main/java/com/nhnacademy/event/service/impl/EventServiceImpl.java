package com.nhnacademy.event.service.impl;

import com.nhnacademy.common.exception.NotFoundException;
import com.nhnacademy.event.domain.Event;
import com.nhnacademy.event.dto.EventCreateRequest;
import com.nhnacademy.event.dto.EventFindRequest;
import com.nhnacademy.event.dto.EventResponse;
import com.nhnacademy.event.repository.EventRepository;
import com.nhnacademy.event.service.EventService;
import com.nhnacademy.eventsource.domain.EventSource;
import com.nhnacademy.eventsource.domain.EventSourceId;
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

    @Override
    public void createEvent(EventCreateRequest eventCreateRequest) {
        EventSourceId eventSourceId = new EventSourceId(
                eventCreateRequest.getSourceId(),
                eventCreateRequest.getSourceType()
        );

        // DB에 존재하면 가져오고, 없으면 새로 저장
        EventSource eventSource = eventSourceRepository.findById(eventSourceId)
                .orElseGet(() -> eventSourceRepository.save(
                        new EventSource(
                                eventCreateRequest.getSourceId(),
                                eventCreateRequest.getSourceType()
                        )
                ));


        Event event = Event.builder()
                .eventDetails(eventCreateRequest.getEventDetails())
                .eventLevel(eventCreateRequest.getEventLevel())
                .eventSource(eventSource)
                .departmentId(eventCreateRequest.getDepartmentId())
                .build();

        eventRepository.save(event);
    }


    @Override
    public void removeEvent(Long eventNo) {
        if (!eventRepository.existsById(eventNo)) {
            throw new NotFoundException("존재하지 않는 eventNo");
        }

        eventRepository.deleteById(eventNo);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<EventResponse> findEvents(EventFindRequest eventFindRequest, Pageable pageable) {
        return eventRepository.findEvents(eventFindRequest, pageable);
    }
}
