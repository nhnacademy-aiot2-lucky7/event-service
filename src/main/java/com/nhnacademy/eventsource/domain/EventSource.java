package com.nhnacademy.eventsource.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "event_source")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@IdClass(EventSourceId.class)
public class EventSource {
    @Id
    @Column(name = "source_id", length = 100, nullable = false)
    @Comment("이벤트 출처 아이디")
    private String sourceId;

    @Id
    @Column(name = "source_type", length = 50)
    @Comment("이벤트 출처 타입")
    private String sourceType;
}