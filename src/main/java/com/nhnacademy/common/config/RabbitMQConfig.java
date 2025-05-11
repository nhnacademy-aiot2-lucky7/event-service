package com.nhnacademy.common.config;

import com.nhnacademy.common.properties.RabbitMQProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private final RabbitMQProperties rabbitMQProperties;

    public RabbitMQConfig(RabbitMQProperties rabbitMQProperties) {
        this.rabbitMQProperties = rabbitMQProperties;
    }

    // 이벤트 생성 큐 정의
    @Bean
    public Queue eventCreateQueue() {
        // 내구성 큐, auto-delete: true (소비자가 없을 때 자동으로 삭제)
        return new Queue(rabbitMQProperties.getQueues().getEventCreateQueue(), true, false, true);
    }

    // 이벤트 생성 큐와 연결될 TopicExchange 정의
    @Bean
    public TopicExchange eventExchange() {
        // 내구성 있는 exchange, auto-delete: true
        return new TopicExchange(rabbitMQProperties.getExchanges().getEventExchange(), true, true);
    }

    // 이벤트 생성 큐와 익스체인지를 바인딩하고, 라우팅 키를 설정하는 바인딩 정의
    @Bean
    public Binding eventCreateBinding() {
        return BindingBuilder.bind(eventCreateQueue())
                .to(eventExchange())
                .with(rabbitMQProperties.getRoutingKeys().getEventCreateRoutingKey());
    }

    // 메시지 변환기를 설정 (JSON 형식으로 메시지를 변환)
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

