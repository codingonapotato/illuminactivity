package com.codingonapotato.illuminactivity.application;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import com.codingonapotato.illuminactivity.application.exception.InvalidPathException;
import com.codingonapotato.illuminactivity.application.exception.InvalidProductNameException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity 
public class Application {
    @Id 
    private String path;

    @Column(name = "product_name")
    private String productName;
    
    private Boolean tracked;

    @OneToMany(mappedBy = "application")
    private List<ApplicationHasCategory> applicationCategories = new ArrayList<>();

    // See https://learn.microsoft.com/en-us/windows/win32/fileio/maximum-file-path-limitation?tabs=registry
    public static int MAX_PATH_LENGTH = 32767;

    public static int MAX_PRODUCT_NAME_LENGTH = 50;
    
    protected Application() {}

    public Application(String path, String productName, Boolean tracked) {
        validateInput(path, productName);
        this.path = path;
        this.productName = productName;
        this.tracked = tracked;
    }
    
    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        validatePath(path);
        this.path = path;
    }

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        validateProductName(productName);
        this.productName = productName;
    }

    public Boolean getTracked() {
        return this.tracked;
    }

    public void setTracked(Boolean tracked) {
        this.tracked = tracked;
    }

    public List<ApplicationHasCategory> getApplicationCategories() {
        return this.applicationCategories;
    }

    private static void validatePath(String path) {
        if (path.length() > MAX_PATH_LENGTH)
            throw new InvalidPathException();
        Path.of(path);       
    }

    private static void validateProductName(String productName) {
        if (productName.length() > MAX_PRODUCT_NAME_LENGTH)
            throw new InvalidProductNameException();
    }

    public static void validateInput(String path, String productName) {
        validatePath(path);
        validateProductName(productName);
    }
}
