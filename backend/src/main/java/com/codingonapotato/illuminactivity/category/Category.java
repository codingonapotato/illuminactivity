package com.codingonapotato.illuminactivity.category;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity 
public class Category {
    @Id 
    private String name;
    private String colour;

    protected Category() {}

    // TODO: Add validation logic
    public Category(String name, String colour) {
        this.name = name;
        this.colour = colour;
    }

    public String getName() {
        return this.name;
    }

    // TODO: Add validation logic
    public void setName(String newName) {
        this.name = newName;
    }

    public String getColour() {
        return this.colour;
    }
    
    // TODO: Add validation logic
    public void setColour(String nextColour) {
        this.colour = nextColour;
    }
}