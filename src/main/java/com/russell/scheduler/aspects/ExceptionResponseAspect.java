package com.russell.scheduler.aspects;

import com.russell.scheduler.dto.ExceptionResponse;
import com.russell.scheduler.exceptions.RecordNotFoundException;
import com.russell.scheduler.exceptions.RecordPersistenceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionResponseAspect {

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
