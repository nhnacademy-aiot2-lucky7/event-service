package com.nhnacademy.event.elasticsearch;

import com.nhnacademy.event.dto.EventResponse;
import com.nhnacademy.event.elasticsearch.document.EventDocument;
import com.nhnacademy.event.elasticsearch.document.EventSourceDocument;
import com.nhnacademy.event.elasticsearch.service.EventSearchService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
class EventSearchServiceTest {
    @Autowired
    private EventSearchService eventSearchService;

    @Test
    @DisplayName("eventDetails 검색으로 여러 이벤트 찾기")
    void testFindByEventDetailsContainingIgnoreCase() {
        // given: 여러 이벤트 데이터를 생성
        List<EventDocument> eventDocuments = Arrays.asList(
                EventDocument.builder()
                        .eventNo(1L)
                        .eventDetails("System Error occurred in the DB layer")
                        .levelName("CRITICAL")
                        .eventAt(LocalDateTime.of(2025, 5, 12, 0, 0, 0, 0))
                        .departmentId("IT001")
                        .eventSource(
                                EventSourceDocument.builder()
                                        .sourceId("SRC123")
                                        .sourceType("SYSTEM")
                                        .build()
                        )
                        .build(),
                EventDocument.builder()
                        .eventNo(2L)
                        .eventDetails("Database connection failure")
                        .levelName("ERROR")
                        .eventAt(LocalDateTime.of(2025, 5, 12, 0, 0, 0, 0))
                        .departmentId("DB_ADMIN")
                        .eventSource(
                                EventSourceDocument.builder()
                                        .sourceId("SRC124")
                                        .sourceType("DB")
                                        .build()
                        )
                        .build(),
                EventDocument.builder()
                        .eventNo(3L)
                        .eventDetails("API connection timeout")
                        .levelName("WARN")
                        .eventAt(LocalDateTime.of(2025, 5, 12, 0, 0, 0, 0))
                        .departmentId("DEVOPS")
                        .eventSource(
                                EventSourceDocument.builder()
                                        .sourceId("SRC125")
                                        .sourceType("API")
                                        .build()
                        )
                        .build()
        );

        eventDocuments.forEach(eventSearchService::saveEvent);

        // when: "DB"라는 단어로 이벤트 검색
        Page<EventResponse> result = eventSearchService
                .searchEventsByDetails(null, "DB", PageRequest.of(0, 10));

        // then: 결과가 비어있지 않으며 "db"라는 단어가 포함되어야 함
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent()).allMatch(event -> event.getEventDetails().toLowerCase().contains("db"));

        // 로그로 출력
        for (EventResponse eventResponse : result.getContent()) {
            log.debug("이벤트 검색 성공: {}", eventResponse);
        }
    }

    @Test
    @DisplayName("departmentId + eventDetails 복합 조건 검색")
    void testFindByDepartmentIdAndEventDetailsContainingIgnoreCase() {
        // given: 여러 이벤트 데이터를 생성
        List<EventDocument> eventDocuments = Arrays.asList(
                EventDocument.builder()
                        .eventNo(1L)
                        .eventDetails("System Error occurred in the DB layer")
                        .levelName("CRITICAL")
                        .eventAt(LocalDateTime.of(2025, 5, 12, 0, 0, 0, 0))
                        .departmentId("IT001")
                        .eventSource(
                                EventSourceDocument.builder()
                                        .sourceId("SRC123")
                                        .sourceType("SYSTEM")
                                        .build()
                        )
                        .build(),
                EventDocument.builder()
                        .eventNo(2L)
                        .eventDetails("API connection timeout")
                        .levelName("WARN")
                        .eventAt(LocalDateTime.of(2025, 5, 12, 0, 0, 0, 0))
                        .departmentId("DEVOPS")
                        .eventSource(
                                EventSourceDocument.builder()
                                        .sourceId("SRC456")
                                        .sourceType("API")
                                        .build()
                        )
                        .build()
        );

        eventDocuments.forEach(eventSearchService::saveEvent);

        // when: departmentId와 eventDetails를 이용한 복합 검색
        Page<EventResponse> result = eventSearchService
                .searchEventsByDetails("DEVOPS", "timeout", PageRequest.of(0, 10));

        // then: 결과가 비어있지 않으며, departmentId가 "DEVOPS"이어야 하고 eventDetails가 "timeout"을 포함해야 함
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().getFirst().getDepartmentId()).isEqualTo("DEVOPS");
        assertThat(result.getContent().getFirst().getEventDetails()).containsIgnoringCase("timeout");

        // 로그로 출력
        for (EventResponse eventResponse : result.getContent()) {
            log.debug("부서명 + 이벤트 검색 성공: {}", eventResponse);
        }
    }
}
