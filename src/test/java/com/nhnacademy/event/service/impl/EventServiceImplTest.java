package com.nhnacademy.event.service.impl;

import com.nhnacademy.adaptor.dto.DepartmentResponse;
import com.nhnacademy.adaptor.dto.UserResponse;
import com.nhnacademy.adaptor.user.UserAdaptor;
import com.nhnacademy.common.exception.ForbiddenException;
import com.nhnacademy.common.exception.NotFoundException;
import com.nhnacademy.common.exception.UnauthorizedException;
import com.nhnacademy.event.domain.Event;
import com.nhnacademy.event.dto.EventCreateRequest;
import com.nhnacademy.event.dto.EventFindRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Test
    @DisplayName("검색 - 어드민은 부서 제한 없이 검색")
    void testSearchEventsByDetails_Admin() {
        when(userAdaptor.getMyInfo()).thenReturn(createUser("ROLE_ADMIN", "dep-01"));
        when(eventSearchService.searchEventsByDetails(null, "오류", Pageable.ofSize(10)))
                .thenReturn(Page.empty());

        eventService.searchEventsByDetails("오류", Pageable.ofSize(10));

        verify(eventSearchService).searchEventsByDetails(null, "오류", Pageable.ofSize(10));
    }

    @Test
    @DisplayName("검색 - 일반 유저는 부서로 제한")
    void testSearchEventsByDetails_User() {
        when(userAdaptor.getMyInfo()).thenReturn(createUser("ROLE_USER", "dep-03"));
        when(eventSearchService.searchEventsByDetails(eq("dep-03"), eq("로그"), any(Pageable.class)))
                .thenReturn(Page.empty());

        eventService.searchEventsByDetails("로그", Pageable.ofSize(5));

        verify(eventSearchService).searchEventsByDetails("dep-03", "로그", Pageable.ofSize(5));
    }

    @Test
    @DisplayName("이벤트 목록 조회 - 관리자가 아닌데 다른 부서 요청 시 Unauthorized")
    void testFindEvents_Unauthorized() {
        when(userAdaptor.getMyInfo()).thenReturn(createUser("ROLE_USER", "dep-01"));

        EventFindRequest request = EventFindRequest.builder()
                .departmentId("dep-02")
                .build();

        assertThrows(UnauthorizedException.class, () -> eventService.findEvents(request, Pageable.ofSize(10)));
    }

    @Test
    @DisplayName("이벤트 목록 조회 - 정상 조회")
    void testFindEvents_Success() {
        when(userAdaptor.getMyInfo()).thenReturn(createUser("ROLE_ADMIN", "dep-01"));

        EventFindRequest request = EventFindRequest.builder()
                .departmentId("dep-02")
                .build();

        List<EventResponse> list = List.of(mock(EventResponse.class));
        Page<EventResponse> page = new PageImpl<>(list);

        when(eventRepository.findEvents(request, Pageable.ofSize(10))).thenReturn(page);

        Page<EventResponse> result = eventService.findEvents(request, Pageable.ofSize(10));

        assertEquals(1, result.getTotalElements());
        verify(eventRepository).findEvents(request, Pageable.ofSize(10));
    }
}