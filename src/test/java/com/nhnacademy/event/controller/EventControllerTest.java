//package com.nhnacademy.event.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.nhnacademy.event.dto.EventFindRequest;
//import com.nhnacademy.event.dto.EventResponse;
//import com.nhnacademy.event.service.EventService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@AutoConfigureMockMvc
//@Transactional
//@AutoConfigureRestDocs
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class EventControllerTest {
//
//    @Autowired
//    private EventService eventService;
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    @DisplayName("이벤트 상세 검색 요청 - 200 OK")
//    void searchEventsByDetails_200() throws Exception {
//        String eventDetails = "에러";
//
//        EventResponse mockResponse = mock(EventResponse.class);
//        when(eventService.searchEventsByDetails(eq(eventDetails), any()))
//                .thenReturn(new PageImpl<>(List.of(mockResponse)));
//
//        mockMvc.perform(get("/events/search-by-details")
//                        .param("eventDetails", eventDetails)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        verify(eventService).searchEventsByDetails(eq(eventDetails), any());
//    }
//
//    @Test
//    @DisplayName("이벤트 전체 조회 요청 - 200 OK")
//    void findAllEvents_200() throws Exception {
//        EventFindRequest request = EventFindRequest.builder()
//                .sourceId("nginx")
//                .build();
//
//        EventResponse mockResponse = mock(EventResponse.class);
//        when(eventService.findEvents(any(EventFindRequest.class), any()))
//                .thenReturn(new PageImpl<>(List.of(mockResponse)));
//
//        mockMvc.perform(post("/events/find-all")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(request)))
//                .andExpect(status().isOk());
//
//        verify(eventService).findEvents(any(EventFindRequest.class), any());
//    }
//
//    @Test
//    @DisplayName("이벤트 삭제 요청 - 204 No Content")
//    void removeEvent_204() throws Exception {
//        Long eventNo = 1L;
//
//        doNothing().when(eventService).removeEvent(eventNo);
//
//        mockMvc.perform(delete("/admin/events/{eventNo}", eventNo))
//                .andExpect(status().isNoContent());
//
//        verify(eventService, times(1)).removeEvent(eventNo);
//    }
//}
