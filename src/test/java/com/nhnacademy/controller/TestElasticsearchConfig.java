package com.nhnacademy.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

@Slf4j
@TestConfiguration
@ActiveProfiles("test")
public class TestElasticsearchConfig {

    @Value("${elasticsearch.host}")
    private String elasticsearchHost;

    private RestClient restClient;

    @Bean(destroyMethod = "close")
    public RestClient restClient() {
        String[] hostPort = elasticsearchHost.split(":");
        String host = hostPort[0];
        int port = Integer.parseInt(hostPort[1]);

        this.restClient = RestClient.builder(new HttpHost(host, port, "http")).build();
        return this.restClient;
    }

    @Bean("testElasticsearchClient")
    public ElasticsearchClient elasticsearchClient(RestClient restClient) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // 이 부분 추가

        JacksonJsonpMapper jacksonJsonpMapper = new JacksonJsonpMapper(objectMapper);
        RestClientTransport transport = new RestClientTransport(restClient, jacksonJsonpMapper);
        return new ElasticsearchClient(transport);
    }

    public void createIndex() throws IOException {
        Request request = new Request("PUT", "/events-lucky7-prod");
        String jsonPayload = """
                {
                  "settings": {
                    "analysis": {
                      "analyzer": {
                        "korean_standard": {
                          "type": "standard"
                        }
                      }
                    }
                  },
                  "mappings": {
                    "properties": {
                      "eventNo": { "type": "long" },
                      "eventDetails": { "type": "text", "analyzer": "korean_standard" },
                      "levelName": { "type": "keyword" },
                      "eventAt": { "type": "date", "format": "strict_date_optional_time||epoch_millis" },
                      "eventSource": {
                        "properties": {
                          "sourceId": { "type": "keyword" },
                          "sourceType": { "type": "keyword" }
                        }
                      },
                      "departmentId": { "type": "keyword" }
                    }
                  }
                }
                
                """;
        request.setJsonEntity(jsonPayload);
        restClient.performRequest(request);
    }
}
