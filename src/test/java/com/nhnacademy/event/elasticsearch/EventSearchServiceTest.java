package com.nhnacademy.event.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.nhnacademy.controller.TestElasticsearchConfig;
import com.nhnacademy.event.dto.EventFindRequest;
import com.nhnacademy.event.dto.EventResponse;
import com.nhnacademy.event.elasticsearch.document.EventDocument;
import com.nhnacademy.event.elasticsearch.document.EventSourceDocument;
import com.nhnacademy.event.elasticsearch.service.EventSearchService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@Import(TestElasticsearchConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventSearchServiceTest {

    @Container
    static ElasticsearchContainer elasticsearchContainer =
            new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.13.0")
                    .withEnv("discovery.type", "single-node")
                    .withEnv("xpack.security.enabled", "false")
                    .withExposedPorts(9200)
                    .waitingFor(Wait.forHttp("/").forStatusCode(200));

    @Autowired
    @Qualifier("testElasticsearchClient")  // 이름 명시해서 주입
    private ElasticsearchClient elasticsearchClient;

    private EventSearchService eventSearchService;

    @Autowired
    private TestElasticsearchConfig testElasticsearchConfig;

    @DynamicPropertySource
    static void elasticsearchProperties(DynamicPropertyRegistry registry) {
        if (!elasticsearchContainer.isRunning()) {
            elasticsearchContainer.start();
        }
        registry.add("elasticsearch.host", () ->
                "127.0.0.1:" + elasticsearchContainer.getFirstMappedPort());
    }


    @BeforeAll
    void setup() throws Exception {
        // 컨테이너가 테스트 시작 전에 반드시 실행되도록 강제 시작
        if (!elasticsearchContainer.isRunning()) {
            elasticsearchContainer.start();
        }

        int retries = 5;
        while (true) {
            try {
                testElasticsearchConfig.createIndex();
                break;
            } catch (IOException e) {
                if (retries-- == 0) throw e;
                Thread.sleep(1000);
            }
        }

        eventSearchService = new EventSearchService(elasticsearchClient);

        List<EventDocument> eventDocuments = Arrays.asList(
                EventDocument.builder()
                        .eventNo(1L)
                        .eventDetails("System Error occurred in the DB layer")  // 1번만 중복
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
                        .eventDetails("Database connection failure detected")  // 다른 내용
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
                        .eventDetails("API response timeout occurred")  // 다르게 변경
                        .levelName("WARN")
                        .eventAt(LocalDateTime.of(2025, 5, 12, 0, 0, 0, 0))
                        .departmentId("DEVOPS")
                        .eventSource(
                                EventSourceDocument.builder()
                                        .sourceId("SRC125")
                                        .sourceType("API")
                                        .build()
                        )
                        .build(),
                EventDocument.builder()
                        .eventNo(4L)
                        .eventDetails("Memory leak detected in system")  // 변경
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
                        .eventNo(5L)
                        .eventDetails("API connection timeout warning")  // 변경
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
        Thread.sleep(1000);
    }

    @Test
    @DisplayName("eventDetails 검색으로 여러 이벤트 찾기")
    void testFindByEventDetailsContainingIgnoreCase() throws InterruptedException {
        EventFindRequest eventFindRequest = EventFindRequest.builder()
                .keyword("DB")
                .build();

        Page<EventResponse> result = eventSearchService
                .searchEventsByDetails(eventFindRequest, PageRequest.of(0, 10));

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent()).allMatch(event -> event.getEventDetails().toLowerCase().contains("db"));

        for (EventResponse eventResponse : result.getContent()) {
            log.info("이벤트 검색 성공: {}", eventResponse);
        }
    }

    @Test
    @DisplayName("departmentId + eventDetails 복합 조건 검색")
    void testFindByDepartmentIdAndEventDetailsContainingIgnoreCase() throws InterruptedException {
        EventFindRequest eventFindRequest = EventFindRequest.builder()
                .departmentId("DEVOPS")
                .keyword("timeout")
                .build();

        Page<EventResponse> result = eventSearchService
                .searchEventsByDetails(eventFindRequest, PageRequest.of(0, 10));

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().getFirst().getDepartmentId()).isEqualTo("DEVOPS");
        assertThat(result.getContent().getFirst().getEventDetails()).containsIgnoringCase("timeout");

        for (EventResponse eventResponse : result.getContent()) {
            log.info("부서명 + 이벤트 검색 성공: {}", eventResponse);
        }
    }
}
