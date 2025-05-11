package com.nhnacademy.repository;

import com.nhnacademy.event.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, CustomEventRepository {
    List<Event> findByEventDetailsContaining(String keyword);
}
