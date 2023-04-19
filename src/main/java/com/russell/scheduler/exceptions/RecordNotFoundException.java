package com.russell.scheduler.exceptions;

public class RecordNotFoundException extends RuntimeException {

    public RecordNotFoundException() {
        super("Resource could not be found with the given search parameters");
    }
}
