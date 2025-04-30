package com.nhnacademy.eventsource.repository;

import com.nhnacademy.eventsource.domain.EventSource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventSourceRepository extends JpaRepository<EventSource, String> {
}
