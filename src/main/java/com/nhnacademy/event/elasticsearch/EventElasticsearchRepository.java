package com.nhnacademy.event.elasticsearch;

import com.nhnacademy.event.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EventElasticsearchRepository extends ElasticsearchRepository<Event, Long> {

    // 페이징 가능한 메서드
    Page<Event> findByDepartmentIdAndEventDetailsContainingIgnoreCase(
            String departmentId, String eventDetails, Pageable pageable
    );

    Page<Event> findByEventDetailsContainingIgnoreCase(String eventDetails, Pageable pageable);
}