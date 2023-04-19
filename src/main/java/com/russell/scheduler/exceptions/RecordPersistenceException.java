package com.russell.scheduler.exceptions;

public class RecordPersistenceException extends RuntimeException {

    public RecordPersistenceException(String message)  {
        super("Resource could not be found with the given search parameters");
    }
}
