package com.nhnacademy.adaptor.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignAuthInterceptor implements RequestInterceptor {

    // 프로퍼티 파일에서 관리자 이메일을 주입받음
    @Value("${admin.email}")
    private String adminEmail;

    @Override
    public void apply(RequestTemplate template) {
        // 요청에서 필요한 정보를 가져오기 위해 현재 스레드에서 요청 속성 정보를 조회
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            String requestUrl = request.getRequestURI(); // 요청 URL을 확인해서 구분할 수 있음
            String userId = request.getHeader("X-User-Id"); // 기본적으로 요청 헤더에서 `X-User-Id` 값 확인

            // URL에 따라 다르게 처리
            if (requestUrl.contains("/admin")) {
                // 관리자 요청이라면 관리자 이메일을 사용
                template.header("X-User-Id", adminEmail);
            } else if (userId != null) {
                // 일반 유저 요청이라면 요청 헤더에서 유저 ID를 가져와서 사용
                template.header("X-User-Id", userId);
            }
        }
    }
}
