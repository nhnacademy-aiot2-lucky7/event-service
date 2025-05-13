package com.nhnacademy.adaptor.interceptor;

import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FeignAuthInterceptorTest {

    private FeignAuthInterceptor interceptor;

    @BeforeEach
    void setUp() throws Exception {
        interceptor = new FeignAuthInterceptor();

        // private 필드인 adminEmail에 값을 주입하기 위해 리플렉션 사용
        Field field = FeignAuthInterceptor.class.getDeclaredField("adminEmail");
        field.setAccessible(true); // private 접근 허용
        field.set(interceptor, "admin@example.com"); // 테스트용 관리자 이메일 설정
    }

    @Test
    @DisplayName("관리자 URL이면 adminEmail이 X-User-Id 헤더에 설정된다")
    void apply_shouldSetAdminEmailHeader_whenAdminUrl() {
        // HttpServletRequest 모킹: 요청 URL이 /admin 포함
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn("/admin/dashboard");

        // RequestContextHolder에 현재 요청 설정
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);

        // Feign 요청 템플릿 생성
        RequestTemplate template = new RequestTemplate();

        // interceptor 실행
        interceptor.apply(template);

        // X-User-Id 헤더가 adminEmail로 설정되었는지 검증
        assertEquals("admin@example.com", template.headers().get("X-User-Id").iterator().next());
    }

    @Test
    @DisplayName("일반 사용자 URL이면 요청 헤더의 X-User-Id가 그대로 전달된다")
    void apply_shouldSetUserIdHeader_whenUserUrl() {
        // HttpServletRequest 모킹: 일반 사용자 URL 및 헤더 설정
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn("/user/info");
        when(mockRequest.getHeader("X-User-Id")).thenReturn("user123");

        // RequestContextHolder에 현재 요청 설정
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);

        // Feign 요청 템플릿 생성
        RequestTemplate template = new RequestTemplate();

        // interceptor 실행
        interceptor.apply(template);

        // X-User-Id 헤더가 요청에서 설정한 user123인지 검증
        assertEquals("user123", template.headers().get("X-User-Id").iterator().next());
    }

    @Test
    @DisplayName("요청 컨텍스트가 없는 경우 X-User-Id 헤더는 설정되지 않는다")
    void apply_shouldDoNothing_whenRequestAttributesAreNull() {
        // RequestContextHolder 초기화 (요청 없음 상태로 만듦)
        RequestContextHolder.resetRequestAttributes();

        // Feign 요청 템플릿 생성
        RequestTemplate template = new RequestTemplate();

        // interceptor 실행
        interceptor.apply(template);

        // X-User-Id 헤더가 설정되지 않았는지 검증
        assertFalse(template.headers().containsKey("X-User-Id"));
    }
}
