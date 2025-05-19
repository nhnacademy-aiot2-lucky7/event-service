package com.nhnacademy.event.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.nhnacademy.event.dto.EventFindRequest;
import com.nhnacademy.event.dto.EventResponse;
import com.nhnacademy.event.dto.EventSourceResponse;
import com.nhnacademy.event.elasticsearch.document.EventDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventSearchService {

    private static final String INDEX = "events-lucky7-prod";
    private final ElasticsearchClient elasticsearchClient;

    // 검색 메소드
    public Page<EventResponse> searchEventsByDetails(EventFindRequest eventFindRequest, Pageable pageable) {
        try {
            // BoolQuery 조건을 동적으로 추가
            Query query = Query.of(q -> q
                    .bool(b -> {
                        mustTermIfNotBlank(b, "departmentId", eventFindRequest.getDepartmentId());
                        mustTermIfNotBlank(b, "eventSource.sourceId", eventFindRequest.getSourceId());
                        mustTermIfNotBlank(b, "eventSource.sourceType", eventFindRequest.getSourceType());
                        mustTermIfNotBlank(b, "levelName", eventFindRequest.getEventLevel());
                        if (eventFindRequest.getKeyword() != null && !eventFindRequest.getKeyword().isEmpty()) {
                            b.must(m -> m.match(t -> t
                                    .field("eventDetails")
                                    .query(eventFindRequest.getKeyword())
                            ));
                        }

                        return b;
                    })
            );

            // 검색 실행
            SearchResponse<EventDocument> response = elasticsearchClient.search(s -> s
                            .index(INDEX)
                            .query(query)
                            .sort(so -> so
                                    .field(f -> f
                                            .field("eventAt")
                                            .order(SortOrder.Desc)
                                    )
                            )
                            .from((int) pageable.getOffset())
                            .size(pageable.getPageSize()),
                    EventDocument.class
            );

            // 검색 결과 매핑
            List<EventResponse> contents = response.hits().hits().stream()
                    .map(Hit::source)
                    .map(event -> new EventResponse(
                            event.getEventNo(),
                            event.getEventDetails(),
                            event.getLevelName(),
                            event.getEventAt(),
                            event.getDepartmentId(),
                            new EventSourceResponse(
                                    event.getEventSource().getSourceId(),
                                    event.getEventSource().getSourceType()
                            )
                    ))
                    .toList();

            long totalHits = response.hits().total() != null
                    ? response.hits().total().value()
                    : contents.size();

            return new PageImpl<>(contents, pageable, totalHits);

        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch query execution failed", e);
        }
    }


    // 저장 메소드
    public void saveEvent(EventDocument eventDocument) {
        try {
            // IndexRequest 생성
            IndexRequest<EventDocument> indexRequest = IndexRequest.of(i -> i
                    .index(INDEX)  // 인덱스 이름 지정
                    .id(String.valueOf(eventDocument.getEventNo()))  // eventNo를 id로 설정
                    .document(eventDocument)  // 저장할 문서 지정
            );

            // Elasticsearch에 데이터 저장
            elasticsearchClient.index(indexRequest);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save document to Elasticsearch", e);
        }
    }

    private void mustTermIfNotBlank(BoolQuery.Builder b, String field, String value) {
        if (value != null && !value.isEmpty()) {
            b.must(m -> m.term(t -> t
                    .field(field)
                    .value(value)
            ));
        }
    }

}
