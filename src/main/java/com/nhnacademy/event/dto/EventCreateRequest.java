package com.nhnacademy.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class EventCreateRequest {
    @JsonProperty("eventLevel")
    @NotBlank(message = "이벤트 레벨은 필수 입력 항목입니다.")
    private String eventLevel;

    @JsonProperty("eventDetails")
    @NotBlank(message = "이벤트 내용은 필수 입력 항목입니다.")
    private String eventDetails;

    @JsonProperty("sourceId")
    @NotBlank(message = "이벤트 출처 아이디는 필수 입력 항목입니다.")
    private String sourceId;

    @JsonProperty("sourceType")
    @NotBlank(message = "이벤트 출처 타입은 필수 입력 항목입니다.")
    private String sourceType;

    @JsonProperty("departmentId")
    @NotBlank(message = "부서 아이디는 필수 입력 항목입니다.")
    private String departmentId;

    @JsonProperty("eventAt")
    @NotBlank(message = "이벤트 발생 일자는 필수 입력 항목입니다.")
    private LocalDateTime eventAt;
}
