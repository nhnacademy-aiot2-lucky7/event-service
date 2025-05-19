package com.nhnacademy.event.listener;

import com.nhnacademy.event.dto.EventCreateRequest;
import com.nhnacademy.event.service.EventService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class EventCreateListenerTest {

    @Test
    void testHandlerCreateEvent_callsEventService() {
        // given
        EventService mockEventService = mock(EventService.class);
        EventCreateListener listener = new EventCreateListener(mockEventService);
        EventCreateRequest request = new EventCreateRequest(
                "ERROR",
                "error message",
                "src1",
                "대시보드",
                "DEP-001",
                LocalDateTime.now()
        );

        // when
        listener.handlerCreateEvent(request);

        // then
        verify(mockEventService, times(1)).createEvent(request);
    }
}
