package com.nhnacademy.event.dto;


import lombok.*;

/**
 * QueryDSL을 사용하여 동적으로 만들었으므로 해당 변수들은 전부 다 입력안해도
 * 입력을 한 변수를 기준으로 이벤트 조회를 실시합니다.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EventFindRequest {

    private String departmentId;

    private String sourceId;

    private String sourceType;

    private String eventLevel;

    private String keyword;
}