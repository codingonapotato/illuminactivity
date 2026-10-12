package com.codingonapotato.illuminactivity.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import com.codingonapotato.illuminactivity.category.exception.InvalidColourException;
import com.codingonapotato.illuminactivity.category.exception.InvalidNameException;

public class CategoryTests {
    @Test
    @DisplayName("Should create instance with specified fields")
    void testConstructor() {
        String name = "Bing Chilling";
        String colour = "#FFFFFF";
        Category category = new Category(name, colour);

        assertEquals(name, category.getName());
        assertEquals(colour, category.getColour());
    }

    @Nested 
    class InputValidationTests {
        @Test
        @DisplayName("should fail with exception due to name that is too short")
        void shouldFailWithShortName() {
            String name = "";
            String colour = "#FFFFFF";
            
            try {
                new Category(name, colour);
                fail("Should have thrown exception");
            } catch (Exception e) {
                assertTrue(e instanceof InvalidNameException);
            }
        }

        @Test
        @DisplayName("should fail with exception due to name that is too long")
        void shouldFailWithLongName() {
            StringBuilder sb = new StringBuilder();
            Collections.nCopies(Category.MAX_NAME_LENGTH + 1, "a").forEach(sb::append);
            
            String name = sb.toString();
            String colour = "#FFFFFF";
            
            try {
                new Category(name, colour);
                fail("Should have thrown exception");
            } catch (Exception e) {
                assertTrue(e instanceof InvalidNameException);
            }
        }

        @Test
        @DisplayName("should fail with exception due to colour not being a 6-character hex code")
        void shouldFailWithInvalidColour() {
            String name = "Bing Chilling";
            String colour = "white";
            
            try {
                new Category(name, colour);
                fail("Should have thrown exception");
            } catch (Exception e) {
                assertTrue(e instanceof InvalidColourException);
            }
        }
    }
}
