package com.nhnacademy.notification.domain;

import com.nhnacademy.event.domain.Event;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notifications",
        indexes = {
                @Index(name = "idx_user_is_read", columnList = "user_no, is_read"),
                @Index(name = "idx_user_no", columnList = "user_no")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private Long userNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_no", nullable = false)
    private Event event;

    private Boolean isRead;

    public void updateRead() {
        this.isRead = true;
    }
}
