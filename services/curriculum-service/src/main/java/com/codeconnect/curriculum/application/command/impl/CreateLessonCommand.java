package com.codeconnect.curriculum.application.command.impl;

import com.codeconnect.curriculum.application.command.CurriculumCommand;
import com.codeconnect.curriculum.application.dto.request.CreateLessonRequest;
import com.codeconnect.curriculum.application.dto.response.LessonResponse;
import com.codeconnect.curriculum.application.mapper.CurriculumMapper;
import com.codeconnect.curriculum.application.service.collaborator.track.ModuleSummaryUpdater;
import com.codeconnect.curriculum.application.validator.CurriculumValidator;
import com.codeconnect.curriculum.domain.event.LessonCreatedEvent;
import com.codeconnect.curriculum.domain.model.LessonDocument;
import com.codeconnect.curriculum.domain.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;

/**
 * Command encapsulating Lesson creation and module lesson count synchronization workflow.
 * Follows the Command Pattern (GoF) and dispatches LessonCreatedEvent.
 */
@Slf4j
@RequiredArgsConstructor
public class CreateLessonCommand implements CurriculumCommand<LessonResponse> {

    private final CreateLessonRequest request;
    private final LessonRepository lessonRepository;
    private final CurriculumValidator validator;
    private final CurriculumMapper mapper;
    private final ModuleSummaryUpdater moduleSummaryUpdater;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public LessonResponse execute() {
        log.info("Executing CreateLessonCommand for moduleId={} slug={}", request.moduleId(), request.slug());
        validator.validateLessonCreation(request);
        LessonDocument savedLesson = lessonRepository.save(mapper.toLessonDocument(request));

        moduleSummaryUpdater.refreshLessonCount(request.trackId(), request.moduleId());

        eventPublisher.publishEvent(new LessonCreatedEvent(
            savedLesson.getId(),
            savedLesson.getModuleId(),
            savedLesson.getTitle(),
            savedLesson.getSlug(),
            savedLesson.getSequence(),
            savedLesson.getCreatedAt() != null ? savedLesson.getCreatedAt() : Instant.now()
        ));

        return mapper.toLessonResponse(savedLesson, null);
    }
}
