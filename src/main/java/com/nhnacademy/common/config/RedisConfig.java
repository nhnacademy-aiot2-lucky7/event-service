package com.nhnacademy.common.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * RedisConfig 클래스는 Spring Data Redis를 설정하는 클래스입니다.
 * <p>
 * Redis 연결 설정을 위한 LettuceConnectionFactory를 제공하며, RedisTemplate을 생성하여 Redis 서버와의 상호작용을 가능하게 합니다.
 * 이 클래스는 Redis 환경 설정을 로드하기 위해 RedisProvider를 사용합니다.
 * </p>
 */
@Slf4j
@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfig {
    @Value("${redis.host}")
    private String host;
    @Value("${redis.port}")
    private int port;
    @Value("${redis.password}")
    private String password;

    @Bean
    public LettuceConnectionFactory notificationRedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setPassword(RedisPassword.of(password));
        config.setDatabase(272);

        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> notificationRedisTemplate(LettuceConnectionFactory notificationRedisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(notificationRedisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}