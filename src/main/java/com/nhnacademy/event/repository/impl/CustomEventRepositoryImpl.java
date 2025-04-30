package com.nhnacademy.event.repository.impl;

import com.nhnacademy.event.domain.Event;
import com.nhnacademy.event.domain.QEvent;
import com.nhnacademy.event.dto.EventResponse;
import com.nhnacademy.event.repository.CustomEventRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Optional;

public class CustomEventRepositoryImpl extends QuerydslRepositorySupport implements CustomEventRepository {

    QEvent qEvent = QEvent.event;

    public CustomEventRepositoryImpl() {
        super(Event.class);
    }

    @Override
    public Page<EventResponse> findByDepartmentIdAndSourceId(String departmentId, String sourceId, Pageable pageable) {
        JPAQuery<EventResponse> query = new JPAQuery<>(getEntityManager());

        List<EventResponse> content = query
                .select(Projections.constructor(
                        EventResponse.class,
                        qEvent.eventDetails,
                        qEvent.eventLevel,
                        qEvent.eventAt,
                        qEvent.eventSource.sourceId
                ))
                .from(qEvent)
                .where(
                        qEvent.departmentId.eq(departmentId),
                        qEvent.eventSource.sourceId.eq(sourceId)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long count = Optional.ofNullable(
                        new JPAQuery<>(getEntityManager())
                                .select(qEvent.count())
                                .from(qEvent)
                                .where(
                                        qEvent.departmentId.eq(departmentId),
                                        qEvent.eventSource.sourceId.eq(sourceId)
                                )
                                .fetchOne())
                .orElse(0L);

        return new PageImpl<>(content, pageable, count);
    }

    @Override
    public Page<EventResponse> findByDepartmentIdAndSourceType(String departmentId, String sourceType, Pageable pageable) {
        JPAQuery<EventResponse> query = new JPAQuery<>(getEntityManager());

        List<EventResponse> content = query
                .select(Projections.constructor(
                        EventResponse.class,
                        qEvent.eventDetails,
                        qEvent.eventLevel,
                        qEvent.eventAt,
                        qEvent.eventSource.sourceId
                ))
                .from(qEvent)
                .where(
                        qEvent.departmentId.eq(departmentId),
                        qEvent.eventSource.sourceType.eq(sourceType)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long count = Optional.ofNullable(
                        new JPAQuery<>(getEntityManager())
                                .select(qEvent.count())
                                .from(qEvent)
                                .where(
                                        qEvent.departmentId.eq(departmentId),
                                        qEvent.eventSource.sourceType.eq(sourceType)
                                )
                                .fetchOne())
                .orElse(0L);

        return new PageImpl<>(content, pageable, count);
    }

    @Override
    public Page<EventResponse> findAllByDepartmentId(String departmentId, Pageable pageable) {
        JPAQuery<EventResponse> query = new JPAQuery<>(getEntityManager());

        List<EventResponse> content = query
                .select(Projections.constructor(
                        EventResponse.class,
                        qEvent.eventDetails,
                        qEvent.eventLevel,
                        qEvent.eventAt,
                        qEvent.eventSource.sourceId
                ))
                .from(qEvent)
                .where(
                        qEvent.departmentId.eq(departmentId)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long count = Optional.ofNullable(
                        new JPAQuery<>(getEntityManager())
                                .select(qEvent.count())
                                .from(qEvent)
                                .where(
                                        qEvent.departmentId.eq(departmentId)
                                )
                                .fetchOne())
                .orElse(0L);

        return new PageImpl<>(content, pageable, count);
    }
}
