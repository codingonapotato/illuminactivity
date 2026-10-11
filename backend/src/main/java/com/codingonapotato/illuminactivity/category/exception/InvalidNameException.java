package com.codingonapotato.illuminactivity.category.exception;

import com.codingonapotato.illuminactivity.category.Category;

public class InvalidNameException extends RuntimeException {
    public InvalidNameException() {
        String message = String.format("Category name must be between %d - %d characters long", 
                            Category.MIN_NAME_LENGTH,
                            Category.MAX_NAME_LENGTH);
        super(message);
    }
}
