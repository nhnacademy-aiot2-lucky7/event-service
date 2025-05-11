package com.nhnacademy.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rabbitmq")
@Getter
@Setter
public class RabbitMQProperties {
    private Queues queues;
    private Exchanges exchanges;
    private RoutingKeys routingKeys;

    // 큐 설정을 위한 내부 클래스
    @Getter
    @Setter
    public static class Queues {
        private String eventCreateQueue;
    }

    // 익스체인지 설정을 위한 내부 클래스
    @Getter
    @Setter
    public static class Exchanges {
        private String eventExchange;
    }

    // 라우팅 키 설정을 위한 내부 클래스
    @Getter
    @Setter
    public static class RoutingKeys {
        private String eventCreateRoutingKey;
    }
}
