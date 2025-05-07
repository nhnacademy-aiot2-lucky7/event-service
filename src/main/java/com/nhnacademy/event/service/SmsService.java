package com.nhnacademy.event.service;

public interface SmsService {
    void sendSms(String toPhoneNumber, String messageContent);
}
