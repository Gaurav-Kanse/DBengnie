package com.gaurav.enigine.exception;

public class VerificationException extends InternalOperationException {
    public static final String PREFIX = "Verification Error: ";

    public VerificationException(String message) {
        super(PREFIX + message);
    }
}
