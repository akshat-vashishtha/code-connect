package com.codeconnect.curriculum.domain.model;

import com.codeconnect.curriculum.domain.enums.TrackStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * CQRS Read Model projection document for curriculum hierarchy (Track -> Modules -> Lessons).
 *
 * <p>Denormalized read store allowing O(1) single-document fetch for the entire curriculum tree,
 * eliminating recursive MongoDB aggregation or multi-collection joins on high-throughput read paths.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "curriculum_tree_projections")
public class CurriculumTreeProjectionDocument {

    @Id
    private String trackId;
    private String title;
    private String slug;
    private String description;
    private Integer estimatedHours;
    private TrackStatus status;

    @Builder.Default
    private List<ModuleTreeItem> modules = new ArrayList<>();

    private Instant lastUpdated;

    public record ModuleTreeItem(
        String moduleId,
        String title,
        String description,
        Integer sequence,
        List<LessonTreeItem> lessons
    ) {}

    public record LessonTreeItem(
        String lessonId,
        String title,
        String slug,
        Integer sequence
    ) {}
}
