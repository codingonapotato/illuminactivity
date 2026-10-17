package com.codingonapotato.illuminactivity.application.exception;

import com.codingonapotato.illuminactivity.application.Application;

public class InvalidPathException extends RuntimeException {
    public InvalidPathException() {
        super(String.format("Path length must not exceed the maximum of %d characters", Application.MAX_PATH_LENGTH));
    }
}
