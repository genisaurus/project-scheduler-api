package com.russell.scheduler.common.exceptions;

public class MissingAuthTokenException extends RuntimeException{
    public MissingAuthTokenException() {
        super("Missing authorization token on request");
    }
}
