package com.codingonapotato.illuminactivity.category;

import java.util.List;
import com.codingonapotato.illuminactivity.application.Application;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity 
public class Category {
    @Id 
    private String name;
    
    private String colour;

    @ManyToMany(mappedBy = "categories")
    private List<Application> applications;
    
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