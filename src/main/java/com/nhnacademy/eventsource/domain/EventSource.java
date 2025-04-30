package com.nhnacademy.eventsource.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "event_source")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EventSource {
    @Id
    @Column(name = "source_id", length = 100, nullable = false)
    @Comment("이벤트 출처 아이디")
    private String sourceId;

    @Column(name = "source_type", length = 50)
    @Comment("이벤트 출처 타입")
    private String sourceType;
}