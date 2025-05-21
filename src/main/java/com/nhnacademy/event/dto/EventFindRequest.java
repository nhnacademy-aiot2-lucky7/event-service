package com.nhnacademy.event.dto;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EventFindRequest {

    private String departmentId;

    private String sourceId;

    private String sourceType;

    private List<String> eventLevels;

    private String keyword;

    private LocalDateTime startAt;

    private LocalDateTime endAt;
}
