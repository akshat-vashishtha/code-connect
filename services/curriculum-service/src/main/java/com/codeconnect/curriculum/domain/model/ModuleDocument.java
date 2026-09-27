package com.codeconnect.curriculum.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "modules")
public class ModuleDocument {
    @Id
    private String id;
    private String trackId;
    private String title;
    private String slug;
    private Integer sequence;
    private String description;
    private String prerequisiteModuleId;
    private Instant createdAt;

    public ModuleDocument() {}

    public ModuleDocument(String id, String trackId, String title, String slug, Integer sequence, String description, String prerequisiteModuleId, Instant createdAt) {
        this.id = id;
        this.trackId = trackId;
        this.title = title;
        this.slug = slug;
        this.sequence = sequence;
        this.description = description;
        this.prerequisiteModuleId = prerequisiteModuleId;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTrackId() { return trackId; }
    public void setTrackId(String trackId) { this.trackId = trackId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public Integer getSequence() { return sequence; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPrerequisiteModuleId() { return prerequisiteModuleId; }
    public void setPrerequisiteModuleId(String prerequisiteModuleId) { this.prerequisiteModuleId = prerequisiteModuleId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static ModuleDocumentBuilder builder() {
        return new ModuleDocumentBuilder();
    }

    public static class ModuleDocumentBuilder {
        private String id;
        private String trackId;
        private String title;
        private String slug;
        private Integer sequence;
        private String description;
        private String prerequisiteModuleId;
        private Instant createdAt;

        public ModuleDocumentBuilder id(String id) { this.id = id; return this; }
        public ModuleDocumentBuilder trackId(String trackId) { this.trackId = trackId; return this; }
        public ModuleDocumentBuilder title(String title) { this.title = title; return this; }
        public ModuleDocumentBuilder slug(String slug) { this.slug = slug; return this; }
        public ModuleDocumentBuilder sequence(Integer sequence) { this.sequence = sequence; return this; }
        public ModuleDocumentBuilder description(String description) { this.description = description; return this; }
        public ModuleDocumentBuilder prerequisiteModuleId(String prerequisiteModuleId) { this.prerequisiteModuleId = prerequisiteModuleId; return this; }
        public ModuleDocumentBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public ModuleDocument build() {
            return new ModuleDocument(id, trackId, title, slug, sequence, description, prerequisiteModuleId, createdAt);
        }
    }
}
