package com.nhnacademy.event.elasticsearch.document;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSourceDocument {
    private String sourceId;
    private String sourceType;
}