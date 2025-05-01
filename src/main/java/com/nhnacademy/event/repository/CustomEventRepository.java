package com.nhnacademy.event.repository;

import com.nhnacademy.event.dto.EventFindRequest;
import com.nhnacademy.event.dto.EventResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomEventRepository {
    Page<EventResponse> findEvents(EventFindRequest eventFindRequest, Pageable pageable);
}
