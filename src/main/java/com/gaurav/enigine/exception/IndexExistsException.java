package com.gaurav.enigine.exception;

public class IndexExistsException extends InternalOperationException {
    public IndexExistsException(String message) {
        super("Index already exists: " + message);
    }
}
