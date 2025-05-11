package com.nhnacademy.common.exception;

public class ForbiddenException extends CommonHttpException {
    private static final int STATUS_CODE = 403;

    public ForbiddenException() {
        super(STATUS_CODE, "어드민이 아닙니다.");
    }
}
