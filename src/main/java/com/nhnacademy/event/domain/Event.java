package com.nhnacademy.event.domain;

import com.nhnacademy.eventsource.domain.EventSource;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Document(indexName = "events") // Elasticsearch 인덱스 설정
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Event {

    @jakarta.persistence.Id // JPA 식별자
    @Id // org.springframework.data.annotation.Id, Elasticsearch 식별자
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_no")
    @Comment("이벤트 번호")
    @Field(type = FieldType.Keyword) // Elasticsearch에서 'Keyword' 타입으로 지정
    private Long eventNo;

    @Column(name = "event_details", columnDefinition = "text", nullable = false)
    @Comment("이벤트 내용")
    @Field(type = FieldType.Text)  // Elasticsearch에서 'text' 필드로 설정
    private String eventDetails;

    @Column(name = "level_name", length = 50, nullable = false)
    @Comment("이벤트 레벨")
    @Field(type = FieldType.Keyword)  // 'Keyword'로 설정하여 필터링, 정렬 가능하게
    private String levelName;

    @Column(name = "event_at", updatable = false)
    @Comment("이벤트 발생 일자")
    @Field(type = FieldType.Date)  // 'Date' 필드로 설정
    private LocalDateTime eventAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "source_id", referencedColumnName = "source_id"),
            @JoinColumn(name = "source_type", referencedColumnName = "source_type")
    })
    private EventSource eventSource;

    @Column(name = "department_id", length = 50, nullable = false)
    @Comment("부서 아이디")
    @Field(type = FieldType.Keyword)  // 'Keyword' 필드로 설정
    private String departmentId;
}
