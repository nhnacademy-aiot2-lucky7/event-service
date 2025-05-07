package com.nhnacademy.event.service.impl;

import com.nhnacademy.event.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    private final DefaultMessageService messageService;
    private final String fromPhoneNumber;

    public SmsServiceImpl(
            @Value("${coolsms.api.key}") String apiKey,
            @Value("${coolsms.api.secret}") String apiSecret,
            @Value("${coolsms.from}") String fromPhoneNumber
    ) {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
        this.fromPhoneNumber = fromPhoneNumber;
    }

    @Override
    public void sendSms(String toPhoneNumber, String messageContent) {
        Message message = new Message();
        message.setFrom(fromPhoneNumber); // 발신번호 (등록된 번호)
        message.setTo(toPhoneNumber);     // 수신번호
        message.setText(messageContent);  // 메시지 내용

        try {
            messageService.send(message);
        } catch (Exception e) {
            throw new RuntimeException("문자 발송 중 에러 발생:" + e.getMessage(), e);
        }
    }
}