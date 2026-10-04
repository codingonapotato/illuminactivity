package com.codingonapotato.illuminactivity.usage;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import com.codingonapotato.illuminactivity.application.ApplicationHasCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity 
@Table(
    name="ApplicationCategoryUsage"
)
public class Usage {
    @EmbeddedId
    private PK pk;

    @ManyToOne(optional = false)
    @MapsId("applicationHasCategoryPK")
    @JoinColumns({
        @JoinColumn(name = "path"),
        @JoinColumn(name = "category")
    })
    private ApplicationHasCategory applicationCategory;

    @Column(name = "end_time", nullable = false)
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
        private ApplicationHasCategory.PK applicationHasCategoryPK;

        @Column(name = "start_time")
        private LocalDateTime startTime;

        public PK(ApplicationHasCategory.PK applicationHasCategoryPK, LocalDateTime startTime) {
            this.applicationHasCategoryPK = applicationHasCategoryPK;
            this.startTime = startTime;
        }

        private PK() {}

        public ApplicationHasCategory.PK getApplicationHasCategoryPK() {
            return this.applicationHasCategoryPK;
        }

        // TODO: Add validation logic
        public void setApplicationHasCategoryPK(ApplicationHasCategory.PK pk) {
            this.applicationHasCategoryPK = pk;
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
            return Objects.equals(applicationHasCategoryPK, pk.applicationHasCategoryPK) &&
                    Objects.equals(startTime, pk.startTime);
        }

        @Override 
        public int hashCode() {
            return Objects.hash(applicationHasCategoryPK, startTime);
        }
    }
}
