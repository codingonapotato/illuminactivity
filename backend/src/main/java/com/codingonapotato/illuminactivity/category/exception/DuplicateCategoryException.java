package com.codingonapotato.illuminactivity.category.exception;

public class DuplicateCategoryException extends RuntimeException {
    public DuplicateCategoryException(String categoryName) {
        super(String.format("Category with name: '%s' already exists", categoryName));
    }
}
