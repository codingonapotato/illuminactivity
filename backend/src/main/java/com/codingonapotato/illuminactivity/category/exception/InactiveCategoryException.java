package com.codingonapotato.illuminactivity.category.exception;

import com.codingonapotato.illuminactivity.category.Category;

public class InactiveCategoryException extends RuntimeException {
    public InactiveCategoryException(Category category) {
        String msg = String.format("Category: %s, is inactive", category.getName());
        super(msg);
    }
}
