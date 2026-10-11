package com.codingonapotato.illuminactivity.category;

import java.util.List;
import java.util.regex.Pattern;
import com.codingonapotato.illuminactivity.application.ApplicationHasCategory;
import com.codingonapotato.illuminactivity.category.exception.InvalidColourException;
import com.codingonapotato.illuminactivity.category.exception.InvalidNameException;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity 
public class Category {
    @Id 
    private String name;
    
    private String colour;

    @OneToMany(mappedBy = "category")
    private List<ApplicationHasCategory> categoryApplications;

    public static final int MIN_NAME_LENGTH = 5;
    public static final int MAX_NAME_LENGTH = 50;
    public static final String COLOUR_FORMAT_PATTERN = "#[A-Fa-f0-9]{6}";

    protected Category() {}

    public Category(String name, String colour) {
        this.name = name;
        this.colour = colour;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public String getColour() {
        return this.colour;
    }
    
    public void setColour(String nextColour) {
        this.colour = nextColour;
    }

    private static void validateName(String name) {
        if (name.length() > MAX_NAME_LENGTH || name.length() < MIN_NAME_LENGTH)
            throw new InvalidNameException();
    }

    private static void validateColour(String colour) {
        if (!Pattern.matches(COLOUR_FORMAT_PATTERN, colour))
            throw new InvalidColourException();
    }

    public static void validateInput(String name, String colour) {
        validateName(name);
        validateColour(colour);
    }
}