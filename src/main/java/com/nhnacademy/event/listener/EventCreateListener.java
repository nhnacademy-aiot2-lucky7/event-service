package com.nhnacademy.event.listener;

import com.nhnacademy.event.dto.EventCreateRequest;
import com.nhnacademy.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventCreateListener {
    private final EventService eventService;

    @RabbitListener(queues = "#{rabbitMQProperties.queues.eventCreateQueue}")
    public void handlerCreateEvent(EventCreateRequest eventCreateRequest) {
        log.debug("event 받음");
        eventService.createEvent(eventCreateRequest);
    }
}
