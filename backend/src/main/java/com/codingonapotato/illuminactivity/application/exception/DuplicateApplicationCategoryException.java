package com.codingonapotato.illuminactivity.application.exception;

import com.codingonapotato.illuminactivity.application.ApplicationHasCategory;

public class DuplicateApplicationCategoryException extends RuntimeException {
    public DuplicateApplicationCategoryException(ApplicationHasCategory applicationCategory) {
        String msg = String.format("An active relationship between the application: %s and the category: %s already exists!",
            applicationCategory.getApplication().getProductName(),
            applicationCategory.getCategory().getName()
        );
        super(msg);
    }    
}
