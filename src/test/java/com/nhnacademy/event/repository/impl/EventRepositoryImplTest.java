package com.nhnacademy.event.repository.impl;

import com.nhnacademy.event.domain.Event;
import com.nhnacademy.event.dto.EventFindRequest;
import com.nhnacademy.event.dto.EventResponse;
import com.nhnacademy.event.repository.EventRepository;
import com.nhnacademy.eventsource.domain.EventSource;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EventRepositoryImplTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        EventSource src1 = new EventSource("src-1", "SYSTEM");
        EventSource src2 = new EventSource("src-2", "USER");
        EventSource src3 = new EventSource("src-3", "API");
        EventSource src4 = new EventSource("src-4", "SCRIPT");
        entityManager.persist(src1);
        entityManager.persist(src2);
        entityManager.persist(src3);
        entityManager.persist(src4);

        List<Event> events = List.of(
                Event.builder().eventDetails("System reboot").levelName("INFO").eventAt(LocalDateTime.of(2025, 5, 1, 8, 0)).eventSource(src1).departmentId("dep-01").build(),
                Event.builder().eventDetails("Login failed").levelName("WARNING").eventAt(LocalDateTime.of(2025, 5, 2, 10, 30)).eventSource(src2).departmentId("dep-02").build(),
                Event.builder().eventDetails("API rate limit").levelName("ERROR").eventAt(LocalDateTime.of(2025, 5, 3, 14, 15)).eventSource(src3).departmentId("dep-03").build(),
                Event.builder().eventDetails("Script timeout").levelName("ERROR").eventAt(LocalDateTime.of(2025, 5, 4, 9, 0)).eventSource(src4).departmentId("dep-04").build(),
                Event.builder().eventDetails("Disk full").levelName("CRITICAL").eventAt(LocalDateTime.of(2025, 5, 5, 6, 0)).eventSource(src1).departmentId("dep-01").build(),

                Event.builder().eventDetails("Session expired").levelName("INFO").eventAt(LocalDateTime.of(2025, 5, 6, 7, 45)).eventSource(src2).departmentId("dep-02").build(),
                Event.builder().eventDetails("Invalid token").levelName("WARNING").eventAt(LocalDateTime.of(2025, 5, 7, 16, 0)).eventSource(src3).departmentId("dep-03").build(),
                Event.builder().eventDetails("Job completed").levelName("INFO").eventAt(LocalDateTime.of(2025, 5, 8, 18, 30)).eventSource(src4).departmentId("dep-04").build(),
                Event.builder().eventDetails("Manual override").levelName("CRITICAL").eventAt(LocalDateTime.of(2025, 5, 9, 13, 0)).eventSource(src2).departmentId("dep-01").build(),
                Event.builder().eventDetails("High CPU usage").levelName("CRITICAL").eventAt(LocalDateTime.of(2025, 5, 10, 10, 10)).eventSource(src1).departmentId("dep-02").build(),

                Event.builder().eventDetails("API downtime").levelName("ERROR").eventAt(LocalDateTime.of(2025, 5, 11, 11, 0)).eventSource(src3).departmentId("dep-03").build(),
                Event.builder().eventDetails("Unauthorized access").levelName("WARNING").eventAt(LocalDateTime.of(2025, 5, 12, 15, 20)).eventSource(src2).departmentId("dep-04").build(),
                Event.builder().eventDetails("Memory leak detected").levelName("ERROR").eventAt(LocalDateTime.of(2025, 5, 13, 12, 0)).eventSource(src1).departmentId("dep-01").build(),
                Event.builder().eventDetails("Database locked").levelName("CRITICAL").eventAt(LocalDateTime.of(2025, 5, 14, 9, 30)).eventSource(src4).departmentId("dep-02").build(),
                Event.builder().eventDetails("Cache miss").levelName("INFO").eventAt(LocalDateTime.of(2025, 5, 15, 10, 0)).eventSource(src1).departmentId("dep-03").build(),

                Event.builder().eventDetails("Token refresh").levelName("INFO").eventAt(LocalDateTime.of(2025, 5, 16, 8, 0)).eventSource(src2).departmentId("dep-04").build(),
                Event.builder().eventDetails("Network issue").levelName("ERROR").eventAt(LocalDateTime.of(2025, 5, 17, 10, 30)).eventSource(src3).departmentId("dep-01").build(),
                Event.builder().eventDetails("Log rotation done").levelName("INFO").eventAt(LocalDateTime.of(2025, 5, 18, 9, 0)).eventSource(src4).departmentId("dep-02").build(),
                Event.builder().eventDetails("Backup succeeded").levelName("INFO").eventAt(LocalDateTime.of(2025, 5, 19, 11, 15)).eventSource(src1).departmentId("dep-03").build(),
                Event.builder().eventDetails("Invalid config").levelName("WARNING").eventAt(LocalDateTime.of(2025, 5, 20, 7, 0)).eventSource(src2).departmentId("dep-04").build()
        );

        events.forEach(entityManager::persist);
        entityManager.flush();
    }

    @Test
    @DisplayName("부서 ID로 이벤트 조회")
    void testFindByDepartmentId() {
        EventFindRequest request = EventFindRequest.builder()
                .departmentId("dep-01")
                .build();

        Page<EventResponse> result = eventRepository.findEvents(request, Pageable.ofSize(10));
        assertFalse(result.isEmpty());
        assertEquals(5, result.getContent().size()); // dep-01 이벤트 5건
    }

    @Test
    @DisplayName("Source ID로 이벤트 조회 및 필터 검증")
    void testFindBySourceId() {
        EventFindRequest request = EventFindRequest.builder()
                .sourceId("src-2")
                .build();

        Page<EventResponse> result = eventRepository.findEvents(request, Pageable.ofSize(10));
        assertFalse(result.isEmpty());
        assertEquals(6, result.getContent().size()); // src-2 이벤트 6건
        assertTrue(result.getContent().stream()
                .allMatch(r -> r.getEventSource().getSourceId().equals("src-2")));
    }

    @Test
    @DisplayName("Source Type으로 이벤트 조회 및 필터 검증")
    void testFindBySourceType() {
        EventFindRequest request = EventFindRequest.builder()
                .sourceType("API")
                .build();

        Page<EventResponse> result = eventRepository.findEvents(request, Pageable.ofSize(10));
        assertFalse(result.isEmpty());
        assertEquals(4, result.getContent().size()); // src-3 = API 타입 이벤트 3건
        assertTrue(result.getContent().stream()
                .allMatch(r -> r.getEventSource().getSourceType().equals("API")));
    }

    @Test
    @DisplayName("이벤트 레벨로 이벤트 조회 및 필터 검증")
    void testFindByEventLevel() {
        EventFindRequest request = EventFindRequest.builder()
                .eventLevel("CRITICAL")
                .build();

        Page<EventResponse> result = eventRepository.findEvents(request, Pageable.ofSize(10));
        assertFalse(result.isEmpty());
        assertEquals(4, result.getContent().size()); // CRITICAL 레벨 이벤트 4건
        assertTrue(result.getContent().stream()
                .allMatch(r -> r.getEventLevel().equals("CRITICAL")));
    }

    @Test
    @DisplayName("복합 조건(부서, 타입, 레벨)으로 이벤트 조회")
    void testFindByMultipleConditions() {
        EventFindRequest request = EventFindRequest.builder()
                .departmentId("dep-03")
                .sourceType("API")
                .eventLevel("ERROR")
                .build();

        Page<EventResponse> result = eventRepository.findEvents(request, Pageable.ofSize(10));
        assertFalse(result.isEmpty());
        assertEquals(2, result.getContent().size()); // 해당 조건에 맞는 건 2건
        assertTrue(result.getContent().stream()
                .allMatch(r ->
                        r.getEventSource().getSourceType().equals("API") &&
                                r.getEventLevel().equals("ERROR")));
    }

    @Test
    @DisplayName("조건 없이 전체 이벤트 조회 (기본 페이지 사이즈)")
    void testFindWithNoConditions() {
        EventFindRequest request = EventFindRequest.builder().build();

        Page<EventResponse> result = eventRepository.findEvents(request, Pageable.ofSize(10));
        assertFalse(result.isEmpty());
        assertEquals(10, result.getContent().size()); // 총 20건 중 1페이지에 10건
    }

    @Test
    @DisplayName("페이징 테스트: 첫 번째 페이지")
    void testPaginationFirstPage() {
        EventFindRequest request = EventFindRequest.builder().build();
        Page<EventResponse> page1 = eventRepository.findEvents(request, Pageable.ofSize(5).withPage(0));

        assertEquals(5, page1.getContent().size()); // 첫 페이지 5건
    }

    @Test
    @DisplayName("페이징 테스트: 두 번째 페이지")
    void testPaginationSecondPage() {
        EventFindRequest request = EventFindRequest.builder().build();
        Page<EventResponse> page2 = eventRepository.findEvents(request, Pageable.ofSize(5).withPage(1));

        assertEquals(5, page2.getContent().size()); // 두 번째 페이지도 5건
    }

}