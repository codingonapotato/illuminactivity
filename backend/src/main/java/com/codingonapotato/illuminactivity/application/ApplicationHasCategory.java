package com.codingonapotato.illuminactivity.application;

import java.io.Serializable;
import java.util.Objects;
import com.codingonapotato.illuminactivity.category.Category;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class ApplicationHasCategory {
    @EmbeddedId
    private PK pk;

    @ManyToOne(optional = false)
    @MapsId("path")
    @JoinColumn(name = "path")
    private Application application;

    @ManyToOne(optional = false)
    @MapsId("category")
    @JoinColumn(name = "category")
    private Category category;

    protected ApplicationHasCategory() {}

    public ApplicationHasCategory(Application application, Category category) {
        this.application = application;
        this.category = category;
        this.pk = new PK(application.getPath(), category.getName());
    }

    public PK getPk() { 
        return pk; 
    }

    public Application getApplication() {
        return application; 
    }

    public Category getCategory() { 
        return category; 
    }

    @Embeddable
    public static class PK implements Serializable {
        private String path;

        private String category;

        public PK(String path, String category) {
            this.path = path;
            this.category = category;
        }

        private PK() {}

        public String getPath() { 
            return path; 
        }

        // TODO: Add validation logic
        public void setPath(String path) {
            this.path = path;
        }

        public String getCategory() { 
            return category; 
        }

        // TODO: Add validation logic
        public void setCategory(String category) {
            this.category = category;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PK that = (PK) o;
            return Objects.equals(path, that.path) && Objects.equals(category, that.category);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path, category);
        }
    }
}
