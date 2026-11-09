package com.codingonapotato.illuminactivity.usage.exception;

public class UsageDateTimeException extends RuntimeException {
    public UsageDateTimeException() {
        super("Usage end time cannot be before start time");
    }
}
