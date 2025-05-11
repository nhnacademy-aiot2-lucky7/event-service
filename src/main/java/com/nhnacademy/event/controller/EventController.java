package com.nhnacademy.event.controller;

import com.nhnacademy.adaptor.dto.UserResponse;
import com.nhnacademy.adaptor.user.UserAdaptor;
import com.nhnacademy.common.exception.ForbiddenException;
import com.nhnacademy.common.exception.NotFoundException;
import com.nhnacademy.event.dto.EventCreateRequest;
import com.nhnacademy.event.dto.EventFindRequest;
import com.nhnacademy.event.dto.EventResponse;
import com.nhnacademy.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final UserAdaptor userAdaptor;

    @PostMapping("/events/find-all")
    public Page<EventResponse> findAllEvents(
            @RequestBody EventFindRequest eventFindRequest,
            @PageableDefault(size = 10, sort = "eventAt", direction = DESC) Pageable pageable) {
        UserResponse userResponse = userAdaptor.getMyInfo();

        // admin이 아닐 경우에는 해당 유저의 departmentId를 강제 주입
        if (!("ROLE_ADMIN").equals(userResponse.getUserRole())) {
            eventFindRequest.setDepartmentId(userResponse.getDepartment().getDepartmentId());
        }

        return eventService.findEvents(eventFindRequest, pageable);
    }

    @PostMapping("/events")
    public ResponseEntity<Void> createEvent(@RequestBody EventCreateRequest eventCreateRequest) {
        eventService.createEvent(eventCreateRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @DeleteMapping("/admin/events/{eventNo}")
    public ResponseEntity<Void> removeEvent(@PathVariable Long eventNo) {
        UserResponse userResponse = userAdaptor.getMyInfo();

        if (userResponse == null) {
            throw new NotFoundException("UserResponse not found");
        }

        if (!("ROLE_ADMIN").equals(userResponse.getUserRole())) {
            throw new ForbiddenException();
        }

        eventService.removeEvent(eventNo);

        return ResponseEntity
                .noContent()
                .build();
    }
}
