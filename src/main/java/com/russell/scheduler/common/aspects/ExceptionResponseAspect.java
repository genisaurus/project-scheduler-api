package com.russell.scheduler.common.aspects;

import com.russell.scheduler.common.dtos.ExceptionResponse;
import com.russell.scheduler.common.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionResponseAspect {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleInvalidRequestException(InvalidRequestException e) {
        return new ExceptionResponse(400, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionResponse handleInvalidCredentialsException(InvalidCredentialsException e) {
        return new ExceptionResponse(401, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionResponse handleInvalidJWTException(InvalidJWTException e) {
        return new ExceptionResponse(401, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionResponse handleAuthorizationException(AuthorizationException e) {
        return new ExceptionResponse(403, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleRecordNotFoundException(RecordNotFoundException e) {
        return new ExceptionResponse(404, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse handleRecordPersistenceException(RecordPersistenceException e) {
        return new ExceptionResponse(409, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleOtherExceptions(Throwable e) {
        return new ExceptionResponse(500, e.getMessage());
    }
}
