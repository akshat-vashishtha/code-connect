package com.codeconnect.curriculum.domain.model;

import com.codeconnect.curriculum.domain.enums.TrackStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "tracks")
public class TrackDocument {
    @Id
    private String id;
    private String title;
    private String slug;
    private String description;
    private Integer estimatedHours;
    private TrackStatus status;
    private List<ModuleSummary> modules = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;

    public TrackDocument() {}

    public TrackDocument(String id, String title, String slug, String description, Integer estimatedHours, TrackStatus status, List<ModuleSummary> modules, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.estimatedHours = estimatedHours;
        this.status = status;
        this.modules = modules != null ? modules : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(Integer estimatedHours) { this.estimatedHours = estimatedHours; }
    public TrackStatus getStatus() { return status; }
    public void setStatus(TrackStatus status) { this.status = status; }
    public List<ModuleSummary> getModules() { return modules; }
    public void setModules(List<ModuleSummary> modules) { this.modules = modules; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static TrackDocumentBuilder builder() {
        return new TrackDocumentBuilder();
    }

    public static class TrackDocumentBuilder {
        private String id;
        private String title;
        private String slug;
        private String description;
        private Integer estimatedHours;
        private TrackStatus status;
        private List<ModuleSummary> modules = new ArrayList<>();
        private Instant createdAt;
        private Instant updatedAt;

        public TrackDocumentBuilder id(String id) { this.id = id; return this; }
        public TrackDocumentBuilder title(String title) { this.title = title; return this; }
        public TrackDocumentBuilder slug(String slug) { this.slug = slug; return this; }
        public TrackDocumentBuilder description(String description) { this.description = description; return this; }
        public TrackDocumentBuilder estimatedHours(Integer estimatedHours) { this.estimatedHours = estimatedHours; return this; }
        public TrackDocumentBuilder status(TrackStatus status) { this.status = status; return this; }
        public TrackDocumentBuilder modules(List<ModuleSummary> modules) { this.modules = modules; return this; }
        public TrackDocumentBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public TrackDocumentBuilder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public TrackDocument build() {
            return new TrackDocument(id, title, slug, description, estimatedHours, status, modules, createdAt, updatedAt);
        }
    }
}
