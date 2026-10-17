package com.codingonapotato.illuminactivity.category.exception;

import com.codingonapotato.illuminactivity.category.Category;

public class InvalidColourException extends RuntimeException {
    public InvalidColourException() {
        String message = String.format("%s: ' %s '. %s",
                            "Category colour must be a 6-digit hex colour code of the format",
                            Category.COLOUR_FORMAT_PATTERN,
                            "For example: #FFFFFF");
        super(message);
    }
}
