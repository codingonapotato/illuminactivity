package com.codingonapotato.illuminactivity.application;

import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity 
public class Application {
    @Id 
    private String path;

    @Column(name = "product_name")
    private String productName;
    
    private Boolean tracked;
    
    protected Application() {}

    public Application(String path, String productName, Boolean tracked) {
        this.path = path;
        this.productName = productName;
        this.tracked = tracked;
    }
    
    public String getPath() {
        return this.path;
    }

    public String getProductName() {
        return this.productName;
    }

    public Boolean getTracked() {
        return this.tracked;
    }

    public void setTracked(Boolean tracked) {
        this.tracked = tracked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Application that = (Application) o;
        return Objects.equals(path, that.path) && 
            Objects.equals(productName, that.productName) &&
            Objects.equals(tracked, that.tracked);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, productName, tracked);
    }
}
