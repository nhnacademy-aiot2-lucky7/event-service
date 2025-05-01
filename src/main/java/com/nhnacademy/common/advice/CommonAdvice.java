package com.nhnacademy.common.advice;

import com.nhnacademy.common.exception.CommonHttpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외를 처리하는 Controller Advice 클래스입니다.
 * <p>
 * 컨트롤러 계층에서 발생할 수 있는 예외를 처리하여 일관된 응답 형식을 제공합니다.
 * </p>
 */
@Slf4j
@RestControllerAdvice
public class CommonAdvice {

    /**
     * {@link BindException} 발생 시 처리하는 메서드입니다.
     * <p>
     * 주로 @Valid, @Validated 어노테이션을 사용한 요청 바인딩 시 유효성 검사에 실패하면 발생합니다.
     * </p>
     *
     * @param e BindException 예외 객체
     * @return 400 Bad Request와 함께 상세 필드 에러 메시지를 포함한 응답
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<String> bindExceptionHandler(BindException e) {
        log.warn("BindException 발생: {}", e.getMessage());

        StringBuilder errorMessage = new StringBuilder("Bad Request: ");

        for (FieldError fieldError : e.getFieldErrors()) {
            errorMessage.append(fieldError.getField())
                    .append(" - ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        }

        return ResponseEntity
                .badRequest()
                .body(errorMessage.toString());
    }

    /**
     * {@link CommonHttpException} 예외 처리 메서드입니다.
     * <p>
     * 커스텀 예외에 포함된 상태 코드를 기반으로 HTTP 응답 상태를 지정합니다.
     * </p>
     *
     * @param e CommonHttpException 예외 객체
     * @return 정의된 상태 코드와 함께 메시지를 포함한 응답
     */
    @ExceptionHandler(CommonHttpException.class)
    public ResponseEntity<String> commonExceptionHandler(CommonHttpException e) {
        log.warn("CommonHttpException 발생: {}", e.getMessage());

        return ResponseEntity
                .status(e.getStatusCode())
                .body("CommonException: " + e.getMessage());
    }

    /**
     * 그 외 모든 예외(Throwable)를 처리하는 메서드입니다.
     * <p>
     * 예상하지 못한 예외가 발생했을 때 서버 내부 에러(500)로 응답합니다.
     * 운영 환경에서는 자세한 정보를 노출하지 않도록 주의합니다.
     * </p>
     *
     * @param e 처리되지 않은 모든 예외
     * @return 500 Internal Server Error 응답
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> exceptionHandler(Throwable e) {
        log.error("Internal Server Error: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("서버에서 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
}
