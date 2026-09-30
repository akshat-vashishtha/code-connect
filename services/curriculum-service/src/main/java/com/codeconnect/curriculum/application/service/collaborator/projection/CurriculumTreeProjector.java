package com.codeconnect.curriculum.application.service.collaborator.projection;

import com.codeconnect.curriculum.domain.event.LessonCreatedEvent;
import com.codeconnect.curriculum.domain.event.TrackCreatedEvent;
import com.codeconnect.curriculum.domain.model.CurriculumTreeProjectionDocument;
import com.codeconnect.curriculum.domain.model.CurriculumTreeProjectionDocument.LessonTreeItem;
import com.codeconnect.curriculum.domain.model.CurriculumTreeProjectionDocument.ModuleTreeItem;
import com.codeconnect.curriculum.domain.model.LessonDocument;
import com.codeconnect.curriculum.domain.model.ModuleDocument;
import com.codeconnect.curriculum.domain.model.TrackDocument;
import com.codeconnect.curriculum.domain.repository.CurriculumTreeProjectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * CQRS Projector responsible for updating the denormalized {@link CurriculumTreeProjectionDocument}
 * read model in response to domain events and batch rebuild requests.
 *
 * <p>Enables single-read queries for the curriculum hierarchy without querying 3 separate collections.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CurriculumTreeProjector {

    private final CurriculumTreeProjectionRepository projectionRepository;

    /**
     * Initializes a read projection document when a new track is created.
     */
    @EventListener
    public void onTrackCreated(TrackCreatedEvent event) {
        log.info("CQRS Projector: initializing curriculum tree projection for trackId={}", event.trackId());

        CurriculumTreeProjectionDocument projection = CurriculumTreeProjectionDocument.builder()
            .trackId(event.trackId())
            .title(event.title())
            .slug(event.slug())
            .description(event.description())
            .estimatedHours(event.estimatedHours())
            .status(event.status())
            .modules(new ArrayList<>())
            .lastUpdated(Instant.now())
            .build();

        projectionRepository.save(projection);
    }

    /**
     * Adds a newly created lesson to the relevant module within the existing read projection.
     */
    @EventListener
    public void onLessonCreated(LessonCreatedEvent event) {
        log.info("CQRS Projector: projecting lessonCreated into tree for lessonId={} moduleId={}",
            event.lessonId(), event.moduleId());

        projectionRepository.findAll().stream()
            .filter(proj -> proj.getModules() != null && proj.getModules().stream().anyMatch(m -> m.moduleId().equals(event.moduleId())))
            .findFirst()
            .ifPresent(proj -> {
                List<ModuleTreeItem> updatedModules = proj.getModules().stream()
                    .map(m -> {
                        if (!m.moduleId().equals(event.moduleId())) {
                            return m;
                        }
                        List<LessonTreeItem> lessons = new ArrayList<>(m.lessons() != null ? m.lessons() : List.of());
                        lessons.add(new LessonTreeItem(event.lessonId(), event.title(), event.slug(), event.sequence()));
                        lessons.sort(Comparator.comparing(LessonTreeItem::sequence, Comparator.nullsLast(Integer::compareTo)));
                        return new ModuleTreeItem(m.moduleId(), m.title(), m.description(), m.sequence(), lessons);
                    })
                    .toList();

                proj.setModules(new ArrayList<>(updatedModules));
                proj.setLastUpdated(Instant.now());
                projectionRepository.save(proj);
            });
    }

    /**
     * Rebuilds or refreshes the full tree projection for a track from source entity documents.
     */
    public CurriculumTreeProjectionDocument projectFullTree(
            TrackDocument track,
            List<ModuleDocument> modules,
            Map<String, List<LessonDocument>> lessonsByModuleId) {

        List<ModuleTreeItem> moduleTreeItems = modules.stream()
            .sorted(Comparator.comparing(ModuleDocument::getSequence, Comparator.nullsLast(Integer::compareTo)))
            .map(mod -> {
                List<LessonDocument> modLessons = lessonsByModuleId.getOrDefault(mod.getId(), List.of());
                List<LessonTreeItem> lessonItems = modLessons.stream()
                    .sorted(Comparator.comparing(LessonDocument::getSequence, Comparator.nullsLast(Integer::compareTo)))
                    .map(les -> new LessonTreeItem(les.getId(), les.getTitle(), les.getSlug(), les.getSequence()))
                    .toList();
                return new ModuleTreeItem(mod.getId(), mod.getTitle(), mod.getDescription(), mod.getSequence(), lessonItems);
            })
            .toList();

        CurriculumTreeProjectionDocument projection = CurriculumTreeProjectionDocument.builder()
            .trackId(track.getId())
            .title(track.getTitle())
            .slug(track.getSlug())
            .description(track.getDescription())
            .estimatedHours(track.getEstimatedHours())
            .status(track.getStatus())
            .modules(new ArrayList<>(moduleTreeItems))
            .lastUpdated(Instant.now())
            .build();

        return projectionRepository.save(projection);
    }
}
