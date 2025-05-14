package com.nhnacademy.event.service.impl;

import com.nhnacademy.common.exception.SmsSendFailedException;
import com.nhnacademy.event.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {
    private final DefaultMessageService messageService;
    private final String fromPhoneNumber;

    @Value("${sms.enabled:false}")
    private boolean smsEnabled;

    public SmsServiceImpl(
            @Value("${coolsms.api.key}") String apiKey,
            @Value("${coolsms.api.secret}") String apiSecret,
            @Value("${coolsms.from}") String fromPhoneNumber
    ) {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
        this.fromPhoneNumber = fromPhoneNumber;
    }


    // 실제 기능은 구현 하였지만 무료 메시지 한도가 있으므로 로그로 전달.
    @Retryable(
            retryFor = {SmsSendFailedException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000) // 2초 간격 재시도
    )
    @Override
    public void sendSms(String toPhoneNumber, String messageContent) {
        Message message = new Message();
        message.setFrom(fromPhoneNumber); // 발신번호 (등록된 번호)
        message.setTo(toPhoneNumber);     // 수신번호
        message.setText(messageContent);  // 메시지 내용

        try {
            if (smsEnabled) {
                messageService.send(message);
            } else {
                log.info("[COOL SMS] 수신번호: {}, 메시지: {}", toPhoneNumber, messageContent);
            }

        } catch (Exception e) {
            log.warn("문자 발송 중 에러 발생:{}", e.getMessage(), e);
            throw new SmsSendFailedException("문자 발송 중 에러 발생", e);
        }
    }

    @Recover
    public void recover(SmsSendFailedException e, String toPhoneNumber, String messageContent) {
        log.error("문자 발송 재시도 모두 실패: 수신번호={}, 메시지 내용={}", toPhoneNumber, messageContent, e);
    }
}