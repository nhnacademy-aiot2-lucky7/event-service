package com.nhnacademy.event.service.impl;

import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = {RabbitAutoConfiguration.class})
@Import(SmsServiceImpl.class)
class SmsServiceImplTest {

    @Autowired
    private SmsServiceImpl smsService;

    private DefaultMessageService mockMessageService;

    @BeforeEach
    void setUp() {
        // mock 객체 생성
        mockMessageService = Mockito.mock(DefaultMessageService.class);

        // 리플렉션으로 private final 필드 설정
        ReflectionTestUtils.setField(smsService, "messageService", mockMessageService);
    }

    @Test
    @DisplayName("정상 문자 발송")
    void sendSms_success() throws Exception {
        // given
        String toPhone = "01098765432";
        String content = "테스트 메시지";

        // when & then
        assertDoesNotThrow(() -> smsService.sendSms(toPhone, content));
        verify(mockMessageService, times(1)).send(any(Message.class));
    }

    @Test
    @DisplayName("문자 발송 재시도 테스트")
    void sendSms_retry() throws Exception {
        // given
        String toPhone = "01000000000";
        String content = "실패 테스트 재시도";

        // 예외 발생하도록 설정 (3번 연속 예외 던짐)
        when(mockMessageService.send(any(Message.class)))
                .thenThrow(new RuntimeException("통신 오류1"))
                .thenThrow(new RuntimeException("통신 오류2"))
                .thenThrow(new RuntimeException("통신 오류3"));

        // when & then
        smsService.sendSms(toPhone, content);

        // verify that the send method is attempted 3 times
        verify(mockMessageService, times(3)).send(any(Message.class));
    }
}
