package com.codingonapotato.illuminactivity.usage;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity 
@Table(
    name="ApplicationCategoryUsage"
)
public class Usage {
    @EmbeddedId
    private PK pk;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    protected Usage() {}

    // TODO: Add validation logic
    public Usage(PK pk, LocalDateTime endTime) {
        this.pk = pk;
        this.endTime = endTime;
    }

    public PK getPK() {
        return pk;
    }

    // TODO: Add validation logic
    public void setPK(PK pk) {
        this.pk = pk;
    }

    public LocalDateTime getEndTime() {
        return this.endTime;
    }
    
    // TODO: Add validation logic
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Embeddable 
    public static class PK implements Serializable {
        private String path;
        
        private String category;

        @Column(name = "start_time")
        private LocalDateTime startTime;

        public PK(String path, String category, LocalDateTime startTime) {
            this.path = path;
            this.category = category;
            this.startTime = startTime;
        }

        private PK() {}

        public String getPath() {
            return this.path;
        }

        // TODO: Add validation logic
        public void setPath(String path) {
            this.path = path;
        }

        public String getCategory() {
            return this.category;
        }

        // TODO: Add validation logic
        public void setCategory(String category) {
            this.category = category;
        }

        public LocalDateTime getStartTime() {
            return this.startTime;
        }
        
        // TODO: Add validation logic
        public void setStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            
            else if (o == null || getClass() != o.getClass()) return false;
            
            PK pk = (PK) o;
            return Objects.equals(path, pk.path) && 
                    Objects.equals(category, pk.category) && 
                    Objects.equals(startTime, pk.startTime);
        }

        @Override 
        public int hashCode() {
            return Objects.hash(path, category, startTime);
        }
    }
}
