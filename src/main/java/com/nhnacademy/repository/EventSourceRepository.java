package com.nhnacademy.repository;

import com.nhnacademy.eventsource.domain.EventSource;
import com.nhnacademy.eventsource.domain.EventSourceId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventSourceRepository extends JpaRepository<EventSource, EventSourceId> {
}
