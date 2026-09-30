package com.codeconnect.curriculum.application.service.collaborator.projection;

import com.codeconnect.curriculum.domain.enums.TrackStatus;
import com.codeconnect.curriculum.domain.event.LessonCreatedEvent;
import com.codeconnect.curriculum.domain.event.TrackCreatedEvent;
import com.codeconnect.curriculum.domain.model.CurriculumTreeProjectionDocument;
import com.codeconnect.curriculum.domain.model.CurriculumTreeProjectionDocument.ModuleTreeItem;
import com.codeconnect.curriculum.domain.model.LessonDocument;
import com.codeconnect.curriculum.domain.model.ModuleDocument;
import com.codeconnect.curriculum.domain.model.TrackDocument;
import com.codeconnect.curriculum.domain.repository.CurriculumTreeProjectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurriculumTreeProjectorTest {

    @Mock
    private CurriculumTreeProjectionRepository projectionRepository;

    private CurriculumTreeProjector projector;

    @BeforeEach
    void setUp() {
        projector = new CurriculumTreeProjector(projectionRepository);
    }

    @Test
    @DisplayName("Should initialize projection document upon TrackCreatedEvent")
    void shouldInitializeProjectionOnTrackCreated() {
        TrackCreatedEvent event = new TrackCreatedEvent(
            "track-1",
            "Java 21 Mastery",
            "java-21",
            "Advanced Java 21",
            30,
            TrackStatus.PUBLISHED,
            Instant.now()
        );

        projector.onTrackCreated(event);

        ArgumentCaptor<CurriculumTreeProjectionDocument> captor = ArgumentCaptor.forClass(CurriculumTreeProjectionDocument.class);
        verify(projectionRepository).save(captor.capture());

        CurriculumTreeProjectionDocument captured = captor.getValue();
        assertThat(captured.getTrackId()).isEqualTo("track-1");
        assertThat(captured.getTitle()).isEqualTo("Java 21 Mastery");
        assertThat(captured.getSlug()).isEqualTo("java-21");
        assertThat(captured.getEstimatedHours()).isEqualTo(30);
        assertThat(captured.getModules()).isEmpty();
    }

    @Test
    @DisplayName("Should project new lesson into existing module within projection")
    void shouldProjectLessonCreatedIntoTree() {
        ModuleTreeItem moduleItem = new ModuleTreeItem("mod-1", "Concurrency", "Virtual threads", 1, new ArrayList<>());
        CurriculumTreeProjectionDocument existingProjection = CurriculumTreeProjectionDocument.builder()
            .trackId("track-1")
            .title("Java 21 Mastery")
            .modules(new ArrayList<>(List.of(moduleItem)))
            .build();

        when(projectionRepository.findAll()).thenReturn(List.of(existingProjection));

        LessonCreatedEvent event = new LessonCreatedEvent(
            "les-10",
            "mod-1",
            "Structured Concurrency",
            "structured-concurrency",
            1,
            Instant.now()
        );

        projector.onLessonCreated(event);

        verify(projectionRepository).save(existingProjection);
        assertThat(existingProjection.getModules().get(0).lessons()).hasSize(1);
        assertThat(existingProjection.getModules().get(0).lessons().get(0).lessonId()).isEqualTo("les-10");
        assertThat(existingProjection.getModules().get(0).lessons().get(0).title()).isEqualTo("Structured Concurrency");
    }

    @Test
    @DisplayName("Should build complete denormalized tree projection from entities")
    void shouldProjectFullTree() {
        TrackDocument track = TrackDocument.builder()
            .id("track-1")
            .title("Full Stack Track")
            .slug("full-stack")
            .description("Full stack track description")
            .estimatedHours(50)
            .status(TrackStatus.PUBLISHED)
            .build();

        ModuleDocument module1 = ModuleDocument.builder()
            .id("mod-1")
            .title("Backend Module")
            .description("Backend fundamentals")
            .sequence(1)
            .build();

        LessonDocument lesson1 = LessonDocument.builder()
            .id("les-1")
            .moduleId("mod-1")
            .title("Spring Boot 3.3")
            .slug("spring-boot-33")
            .sequence(1)
            .build();

        when(projectionRepository.save(any(CurriculumTreeProjectionDocument.class))).thenAnswer(inv -> inv.getArgument(0));

        CurriculumTreeProjectionDocument result = projector.projectFullTree(
            track,
            List.of(module1),
            Map.of("mod-1", List.of(lesson1))
        );

        assertThat(result.getTrackId()).isEqualTo("track-1");
        assertThat(result.getModules()).hasSize(1);
        assertThat(result.getModules().get(0).lessons()).hasSize(1);
        assertThat(result.getModules().get(0).lessons().get(0).lessonId()).isEqualTo("les-1");
        verify(projectionRepository).save(any(CurriculumTreeProjectionDocument.class));
    }
}
