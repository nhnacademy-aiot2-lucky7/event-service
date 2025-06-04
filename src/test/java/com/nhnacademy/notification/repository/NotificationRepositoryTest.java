package com.nhnacademy.notification.repository;

import com.nhnacademy.event.domain.Event;
import com.nhnacademy.event.dto.EventResponse;
import com.nhnacademy.event.repository.EventRepository;
import com.nhnacademy.eventsource.domain.EventSource;
import com.nhnacademy.eventsource.repository.EventSourceRepository;
import com.nhnacademy.notification.NotificationResponse;
import com.nhnacademy.notification.domain.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventSourceRepository eventSourceRepository;

    private Event event;

    @BeforeEach
    void setUp() {
        eventSourceRepository.save(new EventSource("src1", "대시보드"));

        // 테스트용 Event 객체 설정
        event = Event.builder()
                .eventDetails("테스트 이벤트")
                .levelName("INFO")
                .departmentId("HR")
                .eventSource(new EventSource("src1", "대시보드"))
                .eventAt(LocalDateTime.now())
                .build();

        eventRepository.save(event);
    }

    @Test
    @DisplayName("사용자에 대한 읽지 않은 알림 개수를 카운트한다 (최소 10개 알림 저장)")
    void countByUserNoAndIsReadFalseTest() {
        // given: 10개의 알림을 저장
        for (int i = 0; i < 10; i++) {
            notificationRepository.save(Notification.builder()
                    .userNo(1L)
                    .event(event)
                    .isRead(i % 2 == 0) // 짝수는 읽은 상태, 홀수는 읽지 않은 상태
                    .build());
        }

        // when: 읽지 않은 알림의 개수 카운트
        Long unreadCount = notificationRepository.countByUserNoAndIsReadFalse(1L);

        // then: 읽지 않은 알림이 5개 있어야 한다
        assertEquals(5L, unreadCount);
    }

    @Test
    @DisplayName("사용자에 대한 읽은 알림을 삭제한다 (최소 10개 알림 저장)")
    void deleteByUserNoAndIsReadTrueTest() {
        // given: 10개의 알림을 저장
        for (int i = 0; i < 10; i++) {
            notificationRepository.save(Notification.builder()
                    .userNo(1L)
                    .event(event)
                    .isRead(i % 2 == 0) // 짝수는 읽은 상태, 홀수는 읽지 않은 상태
                    .build());
        }

        // when: 읽은 알림을 삭제
        notificationRepository.deleteByUserNoAndIsReadTrue(1L);

        // then: 읽은 알림이 삭제되어야 하고, 읽지 않은 알림은 남아 있어야 한다
        Long unreadCount = notificationRepository.countByUserNoAndIsReadFalse(1L);
        assertEquals(5L, unreadCount);  // 읽지 않은 알림은 여전히 5개 있어야 한다
    }

    @Test
    @DisplayName("사용자 알림 조회 (읽지 않은 알림, 페이징)")
    void findNotificationsTest() {
        // given: 읽지 않은 알림이 10개 이상 있어야 함 (여기서 한 번에 10개를 저장)
        for (int i = 0; i < 10; i++) {
            notificationRepository.save(Notification.builder()
                    .userNo(1L)
                    .event(event)
                    .isRead(false)
                    .build());
        }

        // when: 읽지 않은 알림 조회 (페이징 처리)
        Page<NotificationResponse> result = notificationRepository.findNotifications(1L, false, PageRequest.of(0, 5));

        // then: 결과가 페이징에 맞게 반환되어야 함
        assertEquals(5, result.getContent().size());  // 첫 페이지에서 5개의 알림
        assertEquals(10, result.getTotalElements()); // 전체 알림 개수는 10개
    }

    @Test
    @DisplayName("사용자 알림 조회 (읽은 알림, 페이징)")
    void findReadNotificationsTest() {
        // given: 읽은 알림을 10개 이상 저장
        for (int i = 0; i < 10; i++) {
            notificationRepository.save(Notification.builder()
                    .userNo(1L)
                    .event(event)
                    .isRead(true)
                    .build());
        }

        // when: 읽은 알림 조회 (페이징 처리)
        Page<NotificationResponse> result = notificationRepository.findNotifications(1L, true, PageRequest.of(0, 5));

        // then: 결과가 페이징에 맞게 반환되어야 함
        assertEquals(5, result.getContent().size());  // 첫 페이지에서 5개의 알림
        assertEquals(10, result.getTotalElements()); // 전체 읽은 알림 개수는 10개
    }
}

