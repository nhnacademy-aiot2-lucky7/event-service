package com.nhnacademy.event.dto;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
public class EventSourceResponse {
    private String sourceId;

    private String sourceType;
}
