package com.russell.scheduler.common.exceptions;

public class RecordNotFoundException extends RuntimeException {

    public RecordNotFoundException() {
        super("Record could not be found with the given search parameters");
    }
}
