package com.nhnacademy.event.service;

import com.nhnacademy.event.dto.EventCreateRequest;
import com.nhnacademy.event.dto.EventFindRequest;
import com.nhnacademy.event.dto.EventResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {
    EventResponse getEventByEventNo(Long eventNo);

    void createEvent(EventCreateRequest eventCreateRequest);

    void removeEvent(Long eventNo);

    Page<EventResponse> searchEvents(EventFindRequest eventFindRequest, Pageable pageable);
}
