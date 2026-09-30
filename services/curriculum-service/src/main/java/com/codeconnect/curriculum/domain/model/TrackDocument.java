package com.codeconnect.curriculum.domain.model;

import com.codeconnect.curriculum.domain.enums.TrackStatus;
import com.codeconnect.curriculum.domain.valueobject.ModuleSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Rich domain Aggregate Root representing a curriculum learning track.
 * Encapsulates track invariants and child module summary mutations.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tracks")
public class TrackDocument {

    @Id
    private String id;
    private String title;
    private String slug;
    private String description;
    private Integer estimatedHours;
    private TrackStatus status;

    @Builder.Default
    private List<ModuleSummary> modules = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Appends or replaces an embedded ModuleSummary in this track.
     * Enforces encapsulation over the internal modules collection.
     */
    public void addOrUpdateModuleSummary(ModuleSummary summary) {
        if (this.modules == null) {
            this.modules = new ArrayList<>();
        }
        this.modules.removeIf(m -> m.getId() != null && m.getId().equals(summary.getId()));
        this.modules.add(summary);
        this.updatedAt = Instant.now();
    }

    /**
     * Updates the recorded lesson count for a specific child module.
     */
    public void updateModuleLessonCount(String moduleId, int lessonCount) {
        if (this.modules == null) {
            return;
        }
        for (int i = 0; i < this.modules.size(); i++) {
            ModuleSummary existing = this.modules.get(i);
            if (existing.getId() != null && existing.getId().equals(moduleId)) {
                this.modules.set(i, ModuleSummary.builder()
                    .id(existing.getId())
                    .title(existing.getTitle())
                    .slug(existing.getSlug())
                    .sequence(existing.getSequence())
                    .lessonCount(lessonCount)
                    .build());
                break;
            }
        }
        this.updatedAt = Instant.now();
    }
}

