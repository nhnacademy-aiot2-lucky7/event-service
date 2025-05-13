package com.nhnacademy.event.repository;

import com.nhnacademy.event.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long>, CustomEventRepository {
}
