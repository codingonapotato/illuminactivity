package com.codingonapotato.illuminactivity.application;

import java.util.List;
import com.codingonapotato.illuminactivity.category.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;;

@Entity 
public class Application {
    @Id 
    private String path;

    @Column(name = "product_name")
    private String productName;
    
    private Boolean tracked;

    @ManyToMany
    @JoinTable(
        name = "ApplicationHasCategory",
        joinColumns = @JoinColumn(name = "path"),
        inverseJoinColumns = @JoinColumn(name = "category")
    )
    private List<Category> categories;

    protected Application() {}

    // TODO: Add validation logic
    public Application(String path, String productName, Boolean tracked) {
        this.path = path;
        this.productName = productName;
        this.tracked = tracked;
    }
    
    public String getPath() {
        return this.path;
    }

    // TODO: Add validation logic
    public void setPath(String path) {
        this.path = path;
    }

    public String getProductName() {
        return this.productName;
    }

    // TODO: Add validation logic
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Boolean getTracked() {
        return this.tracked;
    }

    // TODO: Add validation logic
    public void setTracked(Boolean tracked) {
        this.tracked = tracked;
    }

}
