package com.nhnacademy.notification.service.impl;

import com.nhnacademy.adaptor.dto.UserResponse;
import com.nhnacademy.adaptor.user.UserAdaptor;
import com.nhnacademy.common.exception.NotFoundException;
import com.nhnacademy.common.exception.UnauthorizedException;
import com.nhnacademy.event.domain.Event;
import com.nhnacademy.event.domain.EventLevel;
import com.nhnacademy.event.dto.EventResponse;
import com.nhnacademy.event.service.SmsService;
import com.nhnacademy.notification.domain.Notification;
import com.nhnacademy.notification.repository.NotificationRepository;
import com.nhnacademy.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final SmsService smsService;
    private final UserAdaptor userAdaptor;

    @Override
    public EventResponse getNotification(Long notificationNo) {
        Notification notification = notificationRepository.findById(notificationNo)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 알림"));

        notification.updateRead();

        return EventResponse.from(notification.getEvent());
    }

    @Override
    public void saveNotification(Event event) {
        log.info("알림 저장 시작 - 이벤트 ID: {}, 부서 ID: {}", event.getEventNo(), event.getDepartmentId());

        List<Notification> notifications = new ArrayList<>();
        List<UserResponse> userResponses = userAdaptor.findUsersByDepartmentId(event.getDepartmentId());

        for (UserResponse userResponse : userResponses) {
            int userEventLevel = userResponse.getEventLevelResponse().getPriority();
            int eventLevel = EventLevel.valueOf(event.getLevelName()).getLevel();

            log.debug("유저 {}의 이벤트 레벨: {}, 이벤트 레벨: {}", userResponse.getUserNo(), userEventLevel, eventLevel);

            if (eventLevel >= EventLevel.ERROR.getLevel()) {
                smsService.sendSms(userResponse.getUserPhone(), event.getEventDetails());
            }

            if (userEventLevel <= eventLevel) {
                Notification notification = Notification.builder()
                        .userNo(userResponse.getUserNo())
                        .event(event)
                        .build();
                notifications.add(notification);

                log.debug("알림 대상 유저 추가 - userNo: {}", userResponse.getUserNo());
            }
        }

        if (!notifications.isEmpty()) {
            notificationRepository.saveAll(notifications);
            log.info("총 {}건의 알림 저장 완료", notifications.size());
        } else {
            log.info("저장할 알림 없음");
        }
    }

    @Override
    public Page<EventResponse> findNotificationsByReadStatus(boolean isRead, Pageable pageable) {
        Long userNo = getCurrentUserNo();
        log.info("읽음 여부 '{}'로 유저 {}의 알림 조회 요청", isRead, userNo);

        Page<EventResponse> notifications = notificationRepository.findNotifications(userNo, isRead, pageable);

        log.info("조회된 알림 수: {}", notifications.getTotalElements());
        return notifications;
    }

    @Override
    public Long countUnreadNotifications() {
        Long userNo = getCurrentUserNo();
        Long count = notificationRepository.countByUserNoAndIsReadFalse(userNo);

        log.info("유저 {}의 안 읽은 알림 수: {}", userNo, count);
        return count;
    }

    @Override
    public void deleteReadNotifications() {
        Long userNo = getCurrentUserNo();
        log.info("유저 {}의 읽은 알림 삭제 요청", userNo);

        notificationRepository.deleteByUserNoAndIsReadTrue(userNo);

        log.info("유저 {}의 읽은 알림 삭제 완료", userNo);
    }

    private Long getCurrentUserNo() {
        UserResponse userResponse = userAdaptor.getMyInfo();
        if (userResponse == null || userResponse.getUserNo() == null) {
            log.error("현재 로그인한 사용자의 정보를 가져올 수 없습니다.");
            throw new UnauthorizedException("로그인한 사용자 정보를 가져올 수 없습니다.");
        }

        log.debug("현재 사용자 조회 성공 - userNo: {}", userResponse.getUserNo());
        return userResponse.getUserNo();
    }
}

