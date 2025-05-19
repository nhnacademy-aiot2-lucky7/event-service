package com.nhnacademy.event.controller;

import com.nhnacademy.event.dto.EventFindRequest;
import com.nhnacademy.event.dto.EventResponse;
import com.nhnacademy.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping("/events/{event-no}")
    public EventResponse getEventByEventNo(@PathVariable("event-no") Long eventNo) {

        return eventService.getEventByEventNo(eventNo);
    }

    @PostMapping("/events/search")
    public Page<EventResponse> searchEvents(
            @RequestBody EventFindRequest eventFindRequest,
            @PageableDefault(size = 10) Pageable pageable) {
        return eventService.searchEvents(eventFindRequest, pageable);
    }

    @DeleteMapping("/admin/events/{event-no}")
    public ResponseEntity<Void> removeEvent(@PathVariable("event-no") Long eventNo) {
        eventService.removeEvent(eventNo);

        return ResponseEntity
                .noContent()
                .build();
    }
}
