package com.nhnacademy.notification.service.impl;

import com.nhnacademy.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final RedisTemplate<String, Object> template;

    public void setNotification(Long eventNo) {

    }
}
