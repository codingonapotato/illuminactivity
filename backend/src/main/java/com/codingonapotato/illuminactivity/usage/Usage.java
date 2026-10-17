package com.codingonapotato.illuminactivity.usage;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import com.codingonapotato.illuminactivity.application.ApplicationHasCategory;
import com.codingonapotato.illuminactivity.usage.exception.UsageDateTimeException;
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

    public Usage(ApplicationHasCategory applicationHasCategory, LocalDateTime startTime, LocalDateTime endTime) {;
        validateEndTimeIsEqualOrAfterStartTime(startTime, endTime);
        this.applicationCategory = applicationHasCategory;
        this.pk = new PK(applicationHasCategory.getPk(), startTime);
        this.endTime = endTime;
    }

    public PK getPK() {
        return pk;
    }

    public ApplicationHasCategory getApplicationCategory() {
        return this.applicationCategory;
    }

    public LocalDateTime getEndTime() {
        return this.endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        validateEndTimeIsEqualOrAfterStartTime(pk.startTime, endTime);
        this.endTime = endTime;
    }

    public static void validateEndTimeIsEqualOrAfterStartTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime))
            throw new UsageDateTimeException();
    }

    public static void validateInput(ApplicationHasCategory applicationHasCategory, LocalDateTime startTime, LocalDateTime endTime) {
        validateEndTimeIsEqualOrAfterStartTime(startTime, endTime);
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

        @SuppressWarnings("unused")
        private PK() {}

        public ApplicationHasCategory.PK getApplicationHasCategoryPK() {
            return this.applicationHasCategoryPK;
        }

        public LocalDateTime getStartTime() {
            return this.startTime;
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
