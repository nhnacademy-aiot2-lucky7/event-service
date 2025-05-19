package com.nhnacademy.event.service.impl;

import com.nhnacademy.adaptor.dto.DepartmentResponse;
import com.nhnacademy.adaptor.dto.UserResponse;
import com.nhnacademy.adaptor.user.UserAdaptor;
import com.nhnacademy.common.exception.ForbiddenException;
import com.nhnacademy.common.exception.NotFoundException;
import com.nhnacademy.event.domain.Event;
import com.nhnacademy.event.dto.EventCreateRequest;
import com.nhnacademy.event.dto.EventResponse;
import com.nhnacademy.event.elasticsearch.document.EventDocument;
import com.nhnacademy.event.elasticsearch.service.EventSearchService;
import com.nhnacademy.event.repository.EventRepository;
import com.nhnacademy.eventsource.domain.EventSource;
import com.nhnacademy.eventsource.domain.EventSourceId;
import com.nhnacademy.eventsource.repository.EventSourceRepository;
import com.nhnacademy.notification.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class EventServiceImplTest {
    @Mock
    EventRepository eventRepository;
    @Mock
    EventSourceRepository eventSourceRepository;
    @Mock
    NotificationService notificationService;
    @Mock
    UserAdaptor userAdaptor;
    @Mock
    EventSearchService eventSearchService;

    @InjectMocks
    EventServiceImpl eventService;

    private UserResponse createUser(String role, String departmentId) {
        return new UserResponse(
                role,
                1L,
                "테스트유저",
                "test@example.com",
                "010-1234-5678",
                new DepartmentResponse(departmentId, "테스트부서"),
                null
        );
    }

    @Test
    @DisplayName("이벤트 조회 - 정상 케이스")
    void testGetEventByEventNo() {
        Event event = Event.builder().
                eventDetails("System reboot").
                levelName("INFO").
                eventAt(LocalDateTime.now()).
                eventSource(new EventSource("src-1", "SYSTEM")).
                departmentId("dep-01")
                .build();

        when(userAdaptor.getMyInfo()).thenReturn(createUser("ROLE_ADMIN", "dep-01"));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

        EventResponse eventResponse = eventService.getEventByEventNo(1L);

        verify(eventRepository).findById(1L);

        assertEquals("System reboot", eventResponse.getEventDetails());
        assertEquals("dep-01", eventResponse.getDepartmentId());
    }

    @Test
    @DisplayName("이벤트 생성 - 정상 케이스")
    void testCreateEvent() {
        EventCreateRequest request = new EventCreateRequest(
                "ERROR", "디스크 부족", "src-01", "SERVER", "dep-01", LocalDateTime.now()
        );

        when(eventSourceRepository.findById(any(EventSourceId.class)))
                .thenReturn(Optional.empty());

        when(eventSourceRepository.save(any(EventSource.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(eventRepository.save(any(Event.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        eventService.createEvent(request);

        verify(eventSourceRepository).save(any(EventSource.class));
        verify(eventRepository).save(any(Event.class));
        verify(notificationService).saveNotification(any(Event.class));
        verify(eventSearchService).saveEvent(any(EventDocument.class));
    }

    @Test
    @DisplayName("이벤트 삭제 - 관리자 권한")
    void testRemoveEvent_AdminUser() {
        when(userAdaptor.getMyInfo()).thenReturn(createUser("ROLE_ADMIN", "dep-01"));
        when(eventRepository.existsById(1L)).thenReturn(true);

        eventService.removeEvent(1L);

        verify(eventRepository).deleteById(1L);
    }

    @Test
    @DisplayName("이벤트 삭제 - 존재하지 않는 이벤트")
    void testRemoveEvent_NotFound() {
        when(userAdaptor.getMyInfo()).thenReturn(createUser("ROLE_ADMIN", "dep-01"));
        when(eventRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> eventService.removeEvent(1L));
    }

    @Test
    @DisplayName("이벤트 삭제 - 일반 유저 접근 시 Forbidden")
    void testRemoveEvent_Forbidden() {
        when(userAdaptor.getMyInfo()).thenReturn(createUser("ROLE_USER", "dep-01"));

        assertThrows(ForbiddenException.class, () -> eventService.removeEvent(1L));
    }
}