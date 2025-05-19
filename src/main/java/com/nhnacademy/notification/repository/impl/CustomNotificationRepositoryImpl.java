package com.nhnacademy.notification.repository.impl;

import com.nhnacademy.event.dto.EventResponse;
import com.nhnacademy.event.dto.EventSourceResponse;
import com.nhnacademy.notification.domain.Notification;
import com.nhnacademy.notification.domain.QNotification;
import com.nhnacademy.notification.repository.CustomNotificationRepository;
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

public class CustomNotificationRepositoryImpl extends QuerydslRepositorySupport implements CustomNotificationRepository {
    public CustomNotificationRepositoryImpl() {
        super(Notification.class);
    }

    @Override
    public Page<EventResponse> findNotifications(Long userNo, Boolean isRead, Pageable pageable) {

        QNotification qNotification = QNotification.notification;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qNotification.userNo.eq(userNo));
        builder.and(qNotification.isRead.eq(isRead));

        JPAQuery<EventResponse> query = new JPAQuery<>(getEntityManager())
                .select(Projections.constructor(
                        EventResponse.class,
                        qNotification.event.eventNo,
                        qNotification.event.eventDetails,
                        qNotification.event.levelName,
                        qNotification.event.eventAt,
                        qNotification.event.departmentId,
                        Projections.constructor(
                                EventSourceResponse.class,
                                qNotification.event.eventSource.sourceId,
                                qNotification.event.eventSource.sourceType
                        )
                ))
                .from(qNotification)
                .where(builder)
                .orderBy(qNotification.event.eventAt.desc());

        List<EventResponse> content = Objects.requireNonNull(getQuerydsl())
                .applyPagination(pageable, query)
                .fetch();

        long count = Optional.ofNullable(
                new JPAQuery<>(getEntityManager())
                        .select(qNotification.count())
                        .from(qNotification)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, pageable, count);
    }

}
