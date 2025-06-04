package com.nhnacademy.notification.service.impl;

import com.nhnacademy.adaptor.dto.EventLevelResponse;
import com.nhnacademy.adaptor.dto.UserResponse;
import com.nhnacademy.adaptor.user.UserAdaptor;
import com.nhnacademy.common.exception.UnauthorizedException;
import com.nhnacademy.event.domain.Event;
import com.nhnacademy.event.dto.EventResponse;
import com.nhnacademy.event.service.SmsService;
import com.nhnacademy.notification.NotificationResponse;
import com.nhnacademy.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SmsService smsService;

    @Mock
    private UserAdaptor userAdaptor;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Event event;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        event = Event.builder()
                .eventNo(1L)
                .levelName("ERROR")
                .eventDetails("디스크 부족")
                .departmentId("dep-01")
                .build();

        userResponse = new UserResponse(
                "ROLE_USER",
                1L,
                "테스트유저",
                "test@example.com",
                "010-1234-5678",
                null, // DepartmentResponse는 null로 처리
                new EventLevelResponse("ERROR", "에러", 3)
        );
    }

    @Test
    @DisplayName("알림 저장 - 정상 케이스")
    void saveNotification_Test() {
        // given
        when(userAdaptor.findUsersByDepartmentId(any(String.class)))
                .thenReturn(Collections.singletonList(userResponse));


        // when
        notificationService.saveNotification(event);

        // then
        verify(notificationRepository).saveAll(anyList());
        verify(smsService).sendSms(anyString(), anyString());
    }

    @Test
    @DisplayName("알림 조회 - 읽음 여부에 따른 조회")
    void findNotificationsByReadStatus_Test() {
        // given
        Pageable pageable = Pageable.ofSize(10);
        List<NotificationResponse> notifications = List.of(
                new NotificationResponse(1L, "디스크 부족", "ERROR", null, "dep-01", null)
        );
        Page<NotificationResponse> page = new PageImpl<>(notifications);

        when(userAdaptor.getMyInfo())
                .thenReturn(userResponse);
        when(notificationRepository.findNotifications(any(Long.class), any(Boolean.class), any(Pageable.class)))
                .thenReturn(page);

        // when
        Page<NotificationResponse> result = notificationService.findNotificationsByReadStatus(true, pageable);

        // then
        verify(notificationRepository).findNotifications(any(Long.class), any(Boolean.class), any(Pageable.class));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("안 읽은 알림 수 조회")
    void countUnreadNotifications_Test() {
        // given
        when(userAdaptor.getMyInfo())
                .thenReturn(userResponse);
        when(notificationRepository.countByUserNoAndIsReadFalse(any(Long.class)))
                .thenReturn(5L);

        // when
        Long count = notificationService.countUnreadNotifications();

        // then
        assertEquals(5L, count);
        verify(notificationRepository).countByUserNoAndIsReadFalse(any(Long.class));
    }

    @Test
    @DisplayName("읽은 알림 삭제")
    void deleteReadNotifications_Test() {
        // given
        when(userAdaptor.getMyInfo()).thenReturn(userResponse);

        // when
        notificationService.deleteReadNotifications();

        // then
        verify(notificationRepository).deleteByUserNoAndIsReadTrue(any(Long.class));
    }

    @Test
    @DisplayName("유저 조회 - 없는 유저")
    void notFoundUser_Test() {
        // given
        when(userAdaptor.getMyInfo()).thenReturn(null);

        // when
        assertThrows(UnauthorizedException.class, () -> notificationService.deleteReadNotifications());

        // then
        verify(userAdaptor).getMyInfo();
    }
}
