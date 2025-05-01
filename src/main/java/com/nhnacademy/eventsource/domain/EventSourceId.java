package com.nhnacademy.eventsource.domain;

import lombok.*;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class EventSourceId implements Serializable {
    private String sourceId;

    private String sourceType;
}
