package com.codingonapotato.illuminactivity.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Nested
    class SetNameTests {
        @Test
        @DisplayName("should update name with a valid value")
        void shouldSetValidName() {
            Category category = new Category("Bing Chilling", "#FFFFFF");
            String name = "Updated Name";

            category.setName(name);

            assertEquals(name, category.getName());
        }

        @Test
        @DisplayName("should fail with exception due to an invalid name")
        void shouldFailWithInvalidName() {
            Category category = new Category("Bing Chilling", "#FFFFFF");
            String originalName = category.getName();

            assertThrows(InvalidNameException.class, () -> category.setName("Bad"));
            assertEquals(originalName, category.getName());
        }
    }

    @Nested
    class SetColourTests {
        @Test
        @DisplayName("should update colour with a valid value")
        void shouldSetValidColour() {
            Category category = new Category("Bing Chilling", "#FFFFFF");
            String colour = "#000000";

            category.setColour(colour);

            assertEquals(colour, category.getColour());
        }

        @Test
        @DisplayName("should fail with exception due to an invalid colour")
        void shouldFailWithInvalidColour() {
            Category category = new Category("Bing Chilling", "#FFFFFF");
            String originalColour = category.getColour();

            assertThrows(InvalidColourException.class, () -> category.setColour("white"));
            assertEquals(originalColour, category.getColour());
        }
    }
}
