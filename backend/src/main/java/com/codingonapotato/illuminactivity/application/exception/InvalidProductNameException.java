package com.codingonapotato.illuminactivity.application.exception;

import com.codingonapotato.illuminactivity.application.Application;

public class InvalidProductNameException extends RuntimeException {
    public InvalidProductNameException() {
        super(String.format("Product name length must not exceed the maximum of %d characters", Application.MAX_PRODUCT_NAME_LENGTH));
    }
}
