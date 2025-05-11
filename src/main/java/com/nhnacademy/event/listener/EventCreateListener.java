package com.nhnacademy.event.listener;

import com.nhnacademy.event.dto.EventCreateRequest;
import com.nhnacademy.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventCreateListener {
    private final EventService eventService;

    @RabbitListener(queues = "#{rabbitMQProperties.queues.eventCreateQueue}")
    public void handlerCreateEvent(EventCreateRequest eventCreateRequest) {
        eventService.createEvent(eventCreateRequest);
    }
}
