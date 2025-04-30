package com.nhnacademy.event.repository;

import com.nhnacademy.event.dto.EventResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomEventRepository {
    Page<EventResponse> findByDepartmentIdAndSourceId(String departmentId, String sourceId, Pageable pageable);

    Page<EventResponse> findByDepartmentIdAndSourceType(String departmentId, String sourceType, Pageable pageable);

    Page<EventResponse> findAllByDepartmentId(String departmentId, Pageable pageable);
}
