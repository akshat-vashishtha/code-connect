package com.codeconnect.curriculum.application.service;

import com.codeconnect.curriculum.application.command.factory.CurriculumCommandFactory;
import com.codeconnect.curriculum.application.dto.request.*;
import com.codeconnect.curriculum.application.dto.response.*;
import com.codeconnect.curriculum.application.mapper.CurriculumMapper;
import com.codeconnect.curriculum.application.service.collaborator.track.ModuleSummaryUpdater;
import com.codeconnect.curriculum.application.service.impl.AdminCurriculumServiceImpl;
import com.codeconnect.curriculum.application.validator.CurriculumValidator;
import com.codeconnect.curriculum.domain.enums.LanguageMode;
import com.codeconnect.curriculum.domain.enums.TrackStatus;
import com.codeconnect.curriculum.domain.model.LessonDocument;
import com.codeconnect.curriculum.domain.model.ModuleDocument;
import com.codeconnect.curriculum.domain.model.TrackDocument;
import com.codeconnect.curriculum.domain.valueobject.ModuleSummary;
import com.codeconnect.curriculum.domain.valueobject.StoryContent;
import com.codeconnect.curriculum.domain.repository.LessonRepository;
import com.codeconnect.curriculum.domain.repository.ModuleRepository;
import com.codeconnect.curriculum.domain.repository.TrackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminCurriculumServiceImplTest {

    @Mock
    private TrackRepository trackRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private CurriculumValidator curriculumValidator;

    @Mock
    private ModuleSummaryUpdater moduleSummaryUpdater;

    @Mock
    private org.springframework.context.ApplicationEventPublisher eventPublisher;

    @Spy
    private CurriculumMapper curriculumMapper = new CurriculumMapper();

    private AdminCurriculumServiceImpl adminCurriculumService;

    @BeforeEach
    void setUp() {
        CurriculumCommandFactory commandFactory = new CurriculumCommandFactory(
            trackRepository,
            moduleRepository,
            lessonRepository,
            curriculumValidator,
            curriculumMapper,
            moduleSummaryUpdater,
            eventPublisher
        );
        adminCurriculumService = new AdminCurriculumServiceImpl(commandFactory, lessonRepository, curriculumMapper);
    }

    @Test
    @DisplayName("createTrack validates input and persists TrackDocument")
    void createTrack_ShouldValidateAndSave() {
        CreateTrackRequest request = new CreateTrackRequest(
            "Data Structures",
            "dsa",
            "Master arrays, linked lists, and trees",
            50,
            TrackStatus.PUBLISHED
        );

        TrackDocument savedDocument = TrackDocument.builder()
            .id("track-100")
            .title(request.title())
            .slug(request.slug())
            .description(request.description())
            .estimatedHours(request.estimatedHours())
            .status(request.status())
            .modules(new ArrayList<>())
            .build();

        when(trackRepository.save(any(TrackDocument.class))).thenReturn(savedDocument);

        TrackResponse response = adminCurriculumService.createTrack(request);

        verify(curriculumValidator).validateTrackCreation(request);
        verify(eventPublisher).publishEvent(any(com.codeconnect.curriculum.domain.event.TrackCreatedEvent.class));
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("track-100");
        assertThat(response.title()).isEqualTo("Data Structures");
    }

    @Test
    @DisplayName("createModule validates input, persists ModuleDocument, and attaches module summary to track")
    void createModule_ShouldValidateSaveAndAppendSummary() {
        CreateModuleRequest request = new CreateModuleRequest(
            "track-1",
            "Basic Syntax",
            "basic-syntax",
            1,
            "Learn basic syntax",
            null
        );

        ModuleDocument savedModule = ModuleDocument.builder()
            .id("module-10")
            .trackId("track-1")
            .title(request.title())
            .slug(request.slug())
            .sequence(1)
            .build();

        TrackDocument trackDoc = TrackDocument.builder()
            .id("track-1")
            .title("Java Core")
            .modules(new ArrayList<>())
            .build();

        when(moduleRepository.save(any(ModuleDocument.class))).thenReturn(savedModule);
        when(trackRepository.findById("track-1")).thenReturn(Optional.of(trackDoc));

        ModuleResponse response = adminCurriculumService.createModule(request);

        verify(curriculumValidator).validateModuleCreation(request);
        verify(moduleSummaryUpdater).appendModuleSummary(any(TrackDocument.class), any(ModuleSummary.class));
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("module-10");
    }

    @Test
    @DisplayName("createLesson validates input, persists LessonDocument, and increments parent module lesson count")
    void createLesson_ShouldValidateSaveAndIncrementModuleCount() {
        CreateLessonRequest request = new CreateLessonRequest(
            "module-1",
            "track-1",
            "Array Memory Layout",
            "array-memory",
            1,
            Map.of(LanguageMode.ENGLISH, StoryContent.builder().title("Array Analogy").narrative("Narrative text").build()),
            "int[] arr = new int[5];",
            "return arr;",
            null,
            null
        );

        LessonDocument savedLesson = LessonDocument.builder()
            .id("lesson-50")
            .moduleId("module-1")
            .trackId("track-1")
            .title(request.title())
            .slug(request.slug())
            .sequence(1)
            .build();

        when(lessonRepository.save(any(LessonDocument.class))).thenReturn(savedLesson);

        LessonResponse response = adminCurriculumService.createLesson(request);

        verify(curriculumValidator).validateLessonCreation(request);
        verify(moduleSummaryUpdater).refreshLessonCount("track-1", "module-1");
        verify(eventPublisher).publishEvent(any(com.codeconnect.curriculum.domain.event.LessonCreatedEvent.class));
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("lesson-50");
    }
}

