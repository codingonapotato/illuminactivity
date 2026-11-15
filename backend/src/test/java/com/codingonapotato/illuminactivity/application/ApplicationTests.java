package com.codingonapotato.illuminactivity.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ApplicationTests {
    private static final String VALID_PATH = "C:\\Program Files\\Example\\example.exe";
    private static final String VALID_PRODUCT_NAME = "Example";

    @Test
    @DisplayName("Should create instance with specified fields")
    void testConstructor() {
        Application application = new Application(VALID_PATH, VALID_PRODUCT_NAME, true);

        assertEquals(VALID_PATH, application.getPath());
        assertEquals(VALID_PRODUCT_NAME, application.getProductName());
        assertEquals(true, application.getTracked());
    }
}
