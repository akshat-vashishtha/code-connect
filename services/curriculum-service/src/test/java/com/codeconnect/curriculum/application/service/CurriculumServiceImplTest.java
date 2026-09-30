package com.codeconnect.curriculum.application.service;

import com.codeconnect.curriculum.application.dto.response.*;
import com.codeconnect.curriculum.application.mapper.CurriculumMapper;
import com.codeconnect.curriculum.application.service.collaborator.progress.PrerequisiteEvaluationStrategyResolver;
import com.codeconnect.curriculum.application.service.impl.CurriculumServiceImpl;
import com.codeconnect.curriculum.domain.enums.TrackStatus;
import com.codeconnect.curriculum.domain.exception.ResourceNotFoundException;
import com.codeconnect.curriculum.domain.model.LessonDocument;
import com.codeconnect.curriculum.domain.model.ModuleDocument;
import com.codeconnect.curriculum.domain.model.StudentProgressDocument;
import com.codeconnect.curriculum.domain.model.TrackDocument;
import com.codeconnect.curriculum.domain.repository.LessonRepository;
import com.codeconnect.curriculum.domain.repository.ModuleRepository;
import com.codeconnect.curriculum.domain.repository.StudentProgressRepository;
import com.codeconnect.curriculum.domain.repository.TrackRepository;
import com.codeconnect.curriculum.infrastructure.security.SecurityContextAccessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurriculumServiceImplTest {

    @Mock
    private TrackRepository trackRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private StudentProgressRepository progressRepository;

    @Mock
    private PrerequisiteEvaluationStrategyResolver prerequisiteResolver;

    @Mock
    private SecurityContextAccessor securityContextAccessor;

    @Spy
    private CurriculumMapper curriculumMapper = new CurriculumMapper();

    @InjectMocks
    private CurriculumServiceImpl curriculumService;

    private TrackDocument sampleTrack;
    private LessonDocument sampleLesson;

    @BeforeEach
    void setUp() {
        sampleTrack = TrackDocument.builder()
            .id("track-1")
            .title("Java Foundations")
            .slug("java-foundations")
            .description("Master Java fundamentals")
            .estimatedHours(40)
            .status(TrackStatus.PUBLISHED)
            .modules(new ArrayList<>())
            .build();

        sampleLesson = LessonDocument.builder()
            .id("lesson-2")
            .moduleId("module-1")
            .trackId("track-1")
            .title("Variables and Memory Layout")
            .slug("variables-memory")
            .sequence(2)
            .prerequisiteLessonId("lesson-1")
            .storyAnalogies(new HashMap<>())
            .build();
    }

    @Test
    @DisplayName("getPublishedTracks returns list of published tracks")
    void getPublishedTracks_ShouldReturnTracks() {
        when(trackRepository.findByStatus(TrackStatus.PUBLISHED)).thenReturn(List.of(sampleTrack));

        List<TrackResponse> result = curriculumService.getPublishedTracks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo("track-1");
        assertThat(result.get(0).title()).isEqualTo("Java Foundations");
    }

    @Test
    @DisplayName("getTrackById throws ResourceNotFoundException when track does not exist")
    void getTrackById_NotFound_ShouldThrowException() {
        when(trackRepository.findById("invalid-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> curriculumService.getTrackById("invalid-id"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Track not found with ID: invalid-id");
    }

    @Test
    @DisplayName("getModulesByTrackId batch-fetches lessons and groups them into modules without N+1 queries")
    void getModulesByTrackId_ShouldBatchFetchLessons() {
        ModuleDocument module1 = ModuleDocument.builder()
            .id("mod-1")
            .trackId("track-1")
            .title("Module 1")
            .sequence(1)
            .build();
        ModuleDocument module2 = ModuleDocument.builder()
            .id("mod-2")
            .trackId("track-1")
            .title("Module 2")
            .sequence(2)
            .build();

        LessonDocument lesson1 = LessonDocument.builder()
            .id("les-1")
            .moduleId("mod-1")
            .title("Lesson 1")
            .sequence(1)
            .build();
        LessonDocument lesson2 = LessonDocument.builder()
            .id("les-2")
            .moduleId("mod-2")
            .title("Lesson 2")
            .sequence(1)
            .build();

        when(trackRepository.existsById("track-1")).thenReturn(true);
        when(moduleRepository.findByTrackIdOrderBySequenceAsc("track-1")).thenReturn(List.of(module1, module2));
        when(lessonRepository.findByModuleIdIn(List.of("mod-1", "mod-2"))).thenReturn(List.of(lesson1, lesson2));

        List<ModuleResponse> result = curriculumService.getModulesByTrackId("track-1");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).lessons()).hasSize(1);
        assertThat(result.get(0).lessons().get(0).id()).isEqualTo("les-1");
        assertThat(result.get(1).lessons()).hasSize(1);
        assertThat(result.get(1).lessons().get(0).id()).isEqualTo("les-2");
    }

    @Test
    @DisplayName("getLessonById calculates soft prerequisite recommendation badge when uncompleted")
    void getLessonById_UncompletedPrereq_ShouldIncludeRecommendationBadge() {
        PrerequisiteRecommendationResponse badge = new PrerequisiteRecommendationResponse(
            "lesson-1",
            "Intro to JVM",
            false,
            true,
            "Prerequisite Intro to JVM Recommended"
        );

        when(lessonRepository.findById("lesson-2")).thenReturn(Optional.of(sampleLesson));
        when(prerequisiteResolver.resolve(sampleLesson, "user-100")).thenReturn(Optional.of(badge));

        LessonResponse response = curriculumService.getLessonById("lesson-2", "user-100");

        assertThat(response).isNotNull();
        assertThat(response.prerequisiteRecommendation()).isNotNull();
        assertThat(response.prerequisiteRecommendation().isCompleted()).isFalse();
        assertThat(response.prerequisiteRecommendation().isRecommended()).isTrue();
        assertThat(response.prerequisiteRecommendation().recommendationBadgeText())
            .contains("Prerequisite Intro to JVM Recommended");
    }

    @Test
    @DisplayName("getStudentProgress returns empty progress when record does not exist")
    void getStudentProgress_NonExistent_ShouldReturnDefaultEmptyProgress() {
        when(securityContextAccessor.isAuthenticated()).thenReturn(false);
        when(progressRepository.findByUserIdAndTrackId("user-1", "track-1")).thenReturn(Optional.empty());

        StudentProgressResponse response = curriculumService.getStudentProgress("user-1", "track-1");

        assertThat(response).isNotNull();
        assertThat(response.completedLessonIds()).isEmpty();
        assertThat(response.ascentPoints()).isEqualTo(0);
    }
}

