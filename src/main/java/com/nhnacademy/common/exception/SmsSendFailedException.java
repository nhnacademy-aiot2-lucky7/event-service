package com.nhnacademy.common.exception;

public class SmsSendFailedException extends RuntimeException {
    public SmsSendFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}