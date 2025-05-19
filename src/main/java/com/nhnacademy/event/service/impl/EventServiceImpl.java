package com.nhnacademy.event.service.impl;

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
    private final UserAdaptor userAdaptor;
    private final EventSearchService eventSearchService;

    @Override
    public EventResponse getEventByEventNo(Long eventNo) {
        UserResponse user = userAdaptor.getMyInfo();
        log.debug("이벤트 조회 요청: 이벤트번호={}", eventNo);

        Event event = eventRepository.findById(eventNo)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 이벤트: eventNo={}", eventNo);
                    return new NotFoundException("존재하지않는 이벤트");
                });

        if (!user.getDepartment().getDepartmentId().equals(event.getDepartmentId()) &&
                !user.getUserRole().equals("ROLE_ADMIN")) {
            throw new UnauthorizedException("권한이 부족합니다.");
        }

        log.debug("이벤트 조회 성공: eventNo={}", eventNo);
        return EventResponse.from(event);
    }


    // 이벤트 내용으로 검색하는 메서드 (페이징 처리)
    @Override
    public Page<EventResponse> searchEvents(EventFindRequest eventFindRequest, Pageable pageable) {
        UserResponse user = userAdaptor.getMyInfo();
        log.debug("이벤트 검색 요청: 역할={}", user.getUserRole());

        if (!user.getUserRole().equals("ROLE_ADMIN")) {
            eventFindRequest.setDepartmentId(user.getDepartment().getDepartmentId());
            log.debug("일반 사용자이므로 부서 ID 설정됨: {}", eventFindRequest.getDepartmentId());
        }

        Page<EventResponse> result = eventSearchService.searchEventsByDetails(eventFindRequest, pageable);
        log.info("이벤트 검색 완료: 결과 수={}, 페이지 크기={}", result.getTotalElements(), pageable.getPageSize());

        return result;
    }


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

        eventSearchService.saveEvent(EventDocument.from(event));

        log.info("이벤트 저장 완료: eventNo: {}", event.getEventNo());

        // 해당 부서의 유저들 알림 저장
        notificationService.saveNotification(event);
    }

    @Override
    public void removeEvent(Long eventNo) {
        log.debug("이벤트 삭제 시작: eventNo: {}", eventNo);

        UserResponse userResponse = userAdaptor.getMyInfo();

        if (userResponse == null) {
            throw new NotFoundException("UserResponse not found");
        } else if (!("ROLE_ADMIN").equals(userResponse.getUserRole())) {
            throw new ForbiddenException();
        }

        if (!eventRepository.existsById(eventNo)) {
            throw new NotFoundException("존재하지 않는 이벤트");
        }

        eventRepository.deleteById(eventNo);

        log.info("이벤트 삭제 완료: eventNo: {}", eventNo);
    }
}
