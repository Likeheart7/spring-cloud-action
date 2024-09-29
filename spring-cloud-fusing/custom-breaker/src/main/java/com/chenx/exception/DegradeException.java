package com.chenx.exception;

// 降级异常
public class DegradeException extends RuntimeException {
    public DegradeException(String message) {
        super(message);
    }
}
