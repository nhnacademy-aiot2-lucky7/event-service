package com.nhnacademy.common.config;

import com.common.AESUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    @Value("${aes.secret.key}")
    private String secretKey;

    @Bean
    public AESUtil aesUtil() {
        AESUtil aesUtil = new AESUtil();
        aesUtil.setKey(secretKey);
        return aesUtil;
    }
}
