package com.codingonapotato.illuminactivity.application.exception;

import com.codingonapotato.illuminactivity.application.ApplicationHasCategory;

public class InactiveApplicationCategoryException extends RuntimeException {
    
    public InactiveApplicationCategoryException(ApplicationHasCategory applicationCategory) {
        String msg = String.format("There is no active relationship between application: %s and category: %s",
            applicationCategory.getApplication().getProductName(),
            applicationCategory.getCategory().getName()
        );
        super(msg);
    }
}
