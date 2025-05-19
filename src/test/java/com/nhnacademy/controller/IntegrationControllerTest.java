package com.nhnacademy.controller;

import com.nhnacademy.adaptor.dto.DepartmentResponse;
import com.nhnacademy.adaptor.dto.EventLevelResponse;
import com.nhnacademy.adaptor.dto.UserResponse;
import com.nhnacademy.adaptor.user.UserAdaptor;
import com.nhnacademy.event.domain.Event;
import com.nhnacademy.event.elasticsearch.service.EventSearchService;
import com.nhnacademy.event.repository.EventRepository;
import com.nhnacademy.event.service.EventService;
import com.nhnacademy.eventsource.domain.EventSource;
import com.nhnacademy.eventsource.repository.EventSourceRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@AutoConfigureRestDocs
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntegrationControllerTest {
    @Autowired
    private EventService eventService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventSourceRepository eventSourceRepository;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserAdaptor userAdaptor;
    @Autowired
    private EventSearchService eventSearchService;

    @BeforeAll
    void setup() {

        EventSource src1 = new EventSource("src-1", "SYSTEM");
        EventSource src2 = new EventSource("src-2", "USER");
        EventSource src3 = new EventSource("src-3", "API");
        EventSource src4 = new EventSource("src-4", "SCRIPT");

        List<EventSource> eventSources = new ArrayList<>();
        eventSources.add(src1);
        eventSources.add(src2);
        eventSources.add(src3);
        eventSources.add(src4);
        eventSourceRepository.saveAll(eventSources);

        List<Event> events = List.of(
                Event.builder().eventDetails("System reboot completed").levelName("INFO").eventAt(LocalDateTime.of(2025, 5, 1, 8, 0)).eventSource(src1).departmentId("dep-01").build(),
                Event.builder().eventDetails("Login failed - invalid token").levelName("WARNING").eventAt(LocalDateTime.of(2025, 5, 2, 10, 30)).eventSource(src2).departmentId("dep-02").build(),
                Event.builder().eventDetails("API rate limit exceeded").levelName("ERROR").eventAt(LocalDateTime.of(2025, 5, 3, 14, 15)).eventSource(src3).departmentId("dep-03").build(),
                Event.builder().eventDetails("Script execution timeout").levelName("ERROR").eventAt(LocalDateTime.of(2025, 5, 4, 9, 0)).eventSource(src4).departmentId("dep-04").build(),
                Event.builder().eventDetails("Disk full error detected").levelName("CRITICAL").eventAt(LocalDateTime.of(2025, 5, 5, 6, 0)).eventSource(src1).departmentId("dep-01").build(),

                Event.builder().eventDetails("Session expired due to timeout").levelName("INFO").eventAt(LocalDateTime.of(2025, 5, 6, 7, 45)).eventSource(src2).departmentId("dep-02").build(),
                Event.builder().eventDetails("Invalid token used in API request").levelName("WARNING").eventAt(LocalDateTime.of(2025, 5, 7, 16, 0)).eventSource(src3).departmentId("dep-03").build(),
                Event.builder().eventDetails("Job completed with no error").levelName("INFO").eventAt(LocalDateTime.of(2025, 5, 8, 18, 30)).eventSource(src4).departmentId("dep-04").build(),
                Event.builder().eventDetails("Manual override triggered by admin").levelName("CRITICAL").eventAt(LocalDateTime.of(2025, 5, 9, 13, 0)).eventSource(src2).departmentId("dep-01").build(),
                Event.builder().eventDetails("High CPU usage warning").levelName("CRITICAL").eventAt(LocalDateTime.of(2025, 5, 10, 10, 10)).eventSource(src1).departmentId("dep-02").build(),

                Event.builder().eventDetails("API downtime due to network error").levelName("ERROR").eventAt(LocalDateTime.of(2025, 5, 11, 11, 0)).eventSource(src3).departmentId("dep-03").build(),
                Event.builder().eventDetails("Unauthorized access attempt detected").levelName("WARNING").eventAt(LocalDateTime.of(2025, 5, 12, 15, 20)).eventSource(src2).departmentId("dep-04").build(),
                Event.builder().eventDetails("Memory leak detected in system").levelName("ERROR").eventAt(LocalDateTime.of(2025, 5, 13, 12, 0)).eventSource(src1).departmentId("dep-01").build(),
                Event.builder().eventDetails("Database locked due to timeout").levelName("CRITICAL").eventAt(LocalDateTime.of(2025, 5, 14, 9, 30)).eventSource(src4).departmentId("dep-02").build(),
                Event.builder().eventDetails("Cache miss in API layer").levelName("INFO").eventAt(LocalDateTime.of(2025, 5, 15, 10, 0)).eventSource(src1).departmentId("dep-03").build(),

                Event.builder().eventDetails("Token refresh completed").levelName("INFO").eventAt(LocalDateTime.of(2025, 5, 16, 8, 0)).eventSource(src2).departmentId("dep-04").build(),
                Event.builder().eventDetails("Network issue caused API error").levelName("ERROR").eventAt(LocalDateTime.of(2025, 5, 17, 10, 30)).eventSource(src3).departmentId("dep-01").build(),
                Event.builder().eventDetails("Log rotation done successfully").levelName("INFO").eventAt(LocalDateTime.of(2025, 5, 18, 9, 0)).eventSource(src4).departmentId("dep-02").build(),
                Event.builder().eventDetails("Backup succeeded with no error").levelName("INFO").eventAt(LocalDateTime.of(2025, 5, 19, 11, 15)).eventSource(src1).departmentId("dep-03").build(),
                Event.builder().eventDetails("Invalid config error detected").levelName("WARNING").eventAt(LocalDateTime.of(2025, 5, 20, 7, 0)).eventSource(src2).departmentId("dep-04").build()
        );

        eventRepository.saveAll(events);
    }

    private UserResponse createUser(String role, String departmentId) {
        return new UserResponse(
                role,
                1L,
                "테스트유저",
                "test@example.com",
                "010-1234-5678",
                new DepartmentResponse(departmentId, "테스트부서"),
                new EventLevelResponse("ERROR", "에러", 3)
        );
    }

    @Test
    @DisplayName("이벤트 삭제 요청 - 204 No Content")
    void removeEvent_204() throws Exception {
        Long eventNo = 1L;

        when(userAdaptor.getMyInfo()).thenReturn(createUser("ROLE_ADMIN", "dep-01"));

        mockMvc.perform(delete("/admin/events/{eventNo}", eventNo))
                .andExpect(status().isNoContent())
                .andDo(document("remove-event-204"));

    }

    @Test
    @DisplayName("알림 목록 조회 - 200 반환")
    void getNotifications_200() throws Exception {
        when(userAdaptor.getMyInfo()).thenReturn(createUser("ROLE_ADMIN", "dep-01"));

        mockMvc.perform(get("/notifications")
                        .param("isRead", "false")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", notNullValue()))
                .andDo(document("get-notifications-200"));
    }

    @Test
    @DisplayName("안 읽은 알림 개수 조회 - 200 반환")
    void countUnreadNotifications_200() throws Exception {
        when(userAdaptor.getMyInfo()).thenReturn(createUser("ROLE_USER", "dep-01"));

        mockMvc.perform(get("/notifications/unread-count")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(notNullValue()))
                .andDo(document("count-unread-notifications-200"));
    }

    @Test
    @DisplayName("읽은 알림 삭제 - 204 반환")
    void deleteReadNotifications_204() throws Exception {
        when(userAdaptor.getMyInfo()).thenReturn(createUser("ROLE_ADMIN", "dep-01"));
        
        mockMvc.perform(delete("/notifications/read")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(document("delete-read-notifications-204"));
    }
}