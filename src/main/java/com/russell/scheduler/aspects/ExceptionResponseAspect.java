package com.russell.scheduler.aspects;

import com.russell.scheduler.dto.ExceptionResponse;
import com.russell.scheduler.exceptions.RecordNotFoundException;
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
        return new ExceptionResponse(404, e.getMessage(), LocalDateTime.now());
    }
}
