package com.codingonapotato.illuminactivity.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import com.codingonapotato.illuminactivity.application.exception.InvalidPathException;
import com.codingonapotato.illuminactivity.application.exception.InvalidProductNameException;

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

    @Nested
    class ConstructorInputValidationTests {
        @Test
        @DisplayName("should fail with exception due to path that is too long")
        void shouldFailWithLongPath() {
            String path = repeatedString(Application.MAX_PATH_LENGTH + 1);

            assertThrows(InvalidPathException.class,
                () -> new Application(path, VALID_PRODUCT_NAME, true));
        }

        @Test
        @DisplayName("should fail with exception due to an invalid path")
        void shouldFailWithInvalidPath() {
            String path = "\u0000";

            assertThrows(java.nio.file.InvalidPathException.class,
                () -> new Application(path, VALID_PRODUCT_NAME, true));
        }

        @Test
        @DisplayName("should fail with exception due to product name that is too long")
        void shouldFailWithLongProductName() {
            String productName = repeatedString(Application.MAX_PRODUCT_NAME_LENGTH + 1);

            assertThrows(InvalidProductNameException.class,
                () -> new Application(VALID_PATH, productName, true));
        }
    }

    @Nested
    class SetPathTests {
        @Test
        @DisplayName("should update path with a valid value")
        void shouldSetValidPath() {
            Application application = new Application(VALID_PATH, VALID_PRODUCT_NAME, true);
            String path = "C:\\Games\\Example\\example.exe";

            application.setPath(path);

            assertEquals(path, application.getPath());
        }

        @Test
        @DisplayName("should fail with exception due to path that is too long")
        void shouldFailWithLongPath() {
            Application application = new Application(VALID_PATH, VALID_PRODUCT_NAME, true);
            String originalPath = application.getPath();
            String path = repeatedString(Application.MAX_PATH_LENGTH + 1);

            assertThrows(InvalidPathException.class, () -> application.setPath(path));
            assertEquals(originalPath, application.getPath());
        }

        @Test
        @DisplayName("should fail with exception due to an invalid path")
        void shouldFailWithInvalidPath() {
            Application application = new Application(VALID_PATH, VALID_PRODUCT_NAME, true);
            String originalPath = application.getPath();
            String path = "\u0000";

            assertThrows(java.nio.file.InvalidPathException.class, () -> application.setPath(path));
            assertEquals(originalPath, application.getPath());
        }
    }

    @Nested
    class SetProductNameTests {
        @Test
        @DisplayName("should update product name with a valid value")
        void shouldSetValidProductName() {
            Application application = new Application(VALID_PATH, VALID_PRODUCT_NAME, true);
            String productName = "Updated Example";

            application.setProductName(productName);

            assertEquals(productName, application.getProductName());
        }

        @Test
        @DisplayName("should fail with exception due to product name that is too long")
        void shouldFailWithLongProductName() {
            Application application = new Application(VALID_PATH, VALID_PRODUCT_NAME, true);
            String originalProductName = application.getProductName();
            String productName = repeatedString(Application.MAX_PRODUCT_NAME_LENGTH + 1);

            assertThrows(InvalidProductNameException.class,
                () -> application.setProductName(productName));
            assertEquals(originalProductName, application.getProductName());
        }
    }

    private static String repeatedString(int length) {
        StringBuilder builder = new StringBuilder();
        Collections.nCopies(length, "a").forEach(builder::append);
        return builder.toString();
    }
}
