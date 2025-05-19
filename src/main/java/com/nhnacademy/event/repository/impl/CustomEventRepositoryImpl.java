package com.nhnacademy.event.repository.impl;

import com.nhnacademy.event.domain.Event;
import com.nhnacademy.event.domain.QEvent;
import com.nhnacademy.event.dto.EventFindRequest;
import com.nhnacademy.event.dto.EventResponse;
import com.nhnacademy.event.dto.EventSourceResponse;
import com.nhnacademy.event.repository.CustomEventRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CustomEventRepositoryImpl extends QuerydslRepositorySupport implements CustomEventRepository {

    QEvent qEvent = QEvent.event;

    public CustomEventRepositoryImpl() {
        super(Event.class);
    }

    @Override
    public Page<EventResponse> findEvents(EventFindRequest eventFindRequest, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        // departmentId가 존재하면 조건에 추가
        if (eventFindRequest.getDepartmentId() != null) {
            builder.and(qEvent.departmentId.eq(eventFindRequest.getDepartmentId()));
        }
        // sourceId가 존재하면 조건에 추가
        if (eventFindRequest.getSourceId() != null) {
            builder.and(qEvent.eventSource.sourceId.eq(eventFindRequest.getSourceId()));
        }
        // sourceType이 존재하면 조건에 추가
        if (eventFindRequest.getSourceType() != null) {
            builder.and(qEvent.eventSource.sourceType.eq(eventFindRequest.getSourceType()));
        }
        // eventLevel이 존재하면 조건에 추가
        if (eventFindRequest.getEventLevel() != null) {
            builder.and(qEvent.levelName.eq(eventFindRequest.getEventLevel()));
        }

        JPAQuery<EventResponse> query = new JPAQuery<>(getEntityManager())
                .select(Projections.constructor(
                        EventResponse.class,
                        qEvent.eventNo,
                        qEvent.eventDetails,
                        qEvent.levelName,
                        qEvent.eventAt,
                        qEvent.departmentId,
                        Projections.constructor(
                                EventSourceResponse.class,
                                qEvent.eventSource.sourceId,
                                qEvent.eventSource.sourceType
                        )

                ))
                .from(qEvent)
                .where(builder);

        List<EventResponse> content = Objects.requireNonNull(getQuerydsl())
                .applyPagination(pageable, query) // sort 포함해서 자동 적용됨
                .fetch();

        long count = Optional.ofNullable(
                new JPAQuery<>(getEntityManager())
                        .select(qEvent.count())
                        .from(qEvent)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, pageable, count);
    }
}
