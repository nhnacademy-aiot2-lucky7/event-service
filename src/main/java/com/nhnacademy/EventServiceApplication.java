package com.nhnacademy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableFeignClients
@EnableJpaRepositories(basePackages = "com.nhnacademy.repository")
@EnableElasticsearchRepositories(basePackages = "com.nhnacademy.event.elasticsearch")
public class EventServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventServiceApplication.class, args);
    }

}
