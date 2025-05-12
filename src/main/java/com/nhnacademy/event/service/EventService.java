package com.nhnacademy.event.service;

import com.nhnacademy.event.dto.EventCreateRequest;
import com.nhnacademy.event.dto.EventFindRequest;
import com.nhnacademy.event.dto.EventResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {
    void createEvent(EventCreateRequest eventCreateRequest);

    void removeEvent(Long eventNo);

    Page<EventResponse> findEvents(EventFindRequest eventFindRequest, Pageable pageable);

    Page<EventResponse> searchEventsByDetails(String keyword, Pageable pageable);
}
