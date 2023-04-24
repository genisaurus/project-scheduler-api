package com.russell.scheduler.common.exceptions;

public class InvalidJWTException extends RuntimeException {

    public InvalidJWTException(String message) {
        super(message);
    }
}
