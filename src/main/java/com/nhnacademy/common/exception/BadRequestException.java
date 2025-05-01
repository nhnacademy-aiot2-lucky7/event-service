package com.nhnacademy.common.exception;

public class BadRequestException extends CommonHttpException {

    private static final int STATUS_CODE = 400;


    public BadRequestException(String message) {
        super(STATUS_CODE, message);
    }

    public BadRequestException() {
        super(STATUS_CODE, "BadRequest EXCEPTION!!!");
    }
}
