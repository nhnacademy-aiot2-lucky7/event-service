package com.nhnacademy.event.service.impl;

import com.nhnacademy.common.exception.NotFoundException;
import com.nhnacademy.event.domain.Event;
import com.nhnacademy.event.dto.EventCreateRequest;
import com.nhnacademy.event.dto.EventFindRequest;
import com.nhnacademy.event.dto.EventResponse;
import com.nhnacademy.event.repository.EventRepository;
import com.nhnacademy.event.service.EventService;
import com.nhnacademy.eventsource.domain.EventSource;
import com.nhnacademy.eventsource.domain.EventSourceId;
import com.nhnacademy.eventsource.repository.EventSourceRepository;
import com.nhnacademy.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventSourceRepository eventSourceRepository;
    private final NotificationService notificationService;

    @Override
    public void createEvent(EventCreateRequest request) {
        log.info("이벤트 저장 시작: sourceId: {}, sourceType: {}", request.getSourceId(), request.getSourceType());

        EventSourceId sourceId = new EventSourceId(request.getSourceId(), request.getSourceType());

        // EventSource가 존재하지 않으면 새로 생성
        EventSource eventSource = eventSourceRepository.findById(sourceId)
                .orElseGet(() -> eventSourceRepository.save(
                        new EventSource(request.getSourceId(), request.getSourceType()))
                );

        Event event = Event.builder()
                .eventDetails(request.getEventDetails())
                .levelName(request.getEventLevel())
                .eventSource(eventSource)
                .departmentId(request.getDepartmentId())
                .build();

        eventRepository.save(event);

        log.info("이벤트 저장 완료: eventNo: {}", event.getEventNo());

        // 해당 부서의 유저들 알림 저장
        notificationService.saveNotification(event);
    }

    @Override
    public void removeEvent(Long eventNo) {
        log.info("이벤트 삭제 시작: eventNo: {}", eventNo);

        if (!eventRepository.existsById(eventNo)) {
            throw new NotFoundException("존재하지 않는 eventNo");
        }

        eventRepository.deleteById(eventNo);

        log.info("이벤트 삭제 완료: eventNo: {}", eventNo);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<EventResponse> findEvents(EventFindRequest eventFindRequest, Pageable pageable) {
        log.info("이벤트 페이징 조회 시작");

        Page<EventResponse> events = eventRepository.findEvents(eventFindRequest, pageable);

        log.info("이벤트 페이징 조회 완료: totalElements: {}", events.getTotalElements());
        return events;
    }
}
