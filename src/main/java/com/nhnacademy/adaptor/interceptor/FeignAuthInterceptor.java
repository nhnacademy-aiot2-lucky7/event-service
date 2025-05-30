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

    @Value("${admin.email}")
    private String adminEmail;

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs == null) {
            // 비동기 실행 환경 등에서 요청 정보가 없을 경우, 관리자 이메일을 기본 헤더로 설정
            template.header("X-User-Id", adminEmail);
            return;
        }

        HttpServletRequest request = attrs.getRequest();
        String requestUrl = request.getRequestURI();
        String userId = request.getHeader("X-USER-ID");

        if (requestUrl.contains("/admin")) {
            template.header("X-User-Id", adminEmail);
        } else if (userId != null) {
            template.header("X-User-Id", userId);
        }
    }
}
