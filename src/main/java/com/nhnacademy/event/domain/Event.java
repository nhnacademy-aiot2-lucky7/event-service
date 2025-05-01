package com.nhnacademy.event.domain;

import com.nhnacademy.eventsource.domain.EventSource;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_no")
    @Comment("이벤트 번호")
    private Long eventNo;

    @Column(name = "event_details", columnDefinition = "text", nullable = false)
    @Comment("이벤트 내용")
    private String eventDetails;

    @Column(name = "event_level", length = 50, nullable = false)
    @Comment("이벤트 레벨")
    private String eventLevel;

    @Column(name = "event_at", updatable = false)
    @Comment("이벤트 발생 일자")
    private LocalDateTime eventAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "source_id", referencedColumnName = "source_id"),
            @JoinColumn(name = "source_type", referencedColumnName = "source_type")
    })
    private EventSource eventSource;

    @Column(name = "department_id", length = 50, nullable = false)
    @Comment("부서 아이디")
    private String departmentId;
}
