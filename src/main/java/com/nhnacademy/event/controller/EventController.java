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

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping("/events/search-by-details")
    public Page<EventResponse> searchEventsByDetails(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable) {
        return eventService.searchEventsByDetails(keyword, pageable);
    }

    @PostMapping("/events/find-all")
    public Page<EventResponse> findAllEvents(
            @RequestBody EventFindRequest eventFindRequest,
            @PageableDefault(size = 10, sort = "eventAt", direction = DESC) Pageable pageable) {

        return eventService.findEvents(eventFindRequest, pageable);
    }

    @DeleteMapping("/admin/events/{eventNo}")
    public ResponseEntity<Void> removeEvent(@PathVariable Long eventNo) {
        eventService.removeEvent(eventNo);

        return ResponseEntity
                .noContent()
                .build();
    }
}
