package com.codingonapotato.illuminactivity.usage.exception;

import com.codingonapotato.illuminactivity.application.ApplicationHasCategory;
import com.codingonapotato.illuminactivity.usage.Usage;

public class DuplicateUsageException extends RuntimeException {
    public DuplicateUsageException(Usage usage) {
        ApplicationHasCategory applicationCategory = usage.getApplicationCategory();
        String msg = String.format(
            "A usage record for application: %s and the category: %s, starting at %s already exists!",
            applicationCategory.getApplication().getProductName(),
            applicationCategory.getCategory().getName(),
            usage.getPK().getStartTime().toString()
        );
        super(msg);
    }
}
