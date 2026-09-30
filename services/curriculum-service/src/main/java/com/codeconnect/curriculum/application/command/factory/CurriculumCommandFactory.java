package com.codeconnect.curriculum.application.command.factory;

import com.codeconnect.curriculum.application.command.impl.CreateLessonCommand;
import com.codeconnect.curriculum.application.command.impl.CreateModuleCommand;
import com.codeconnect.curriculum.application.command.impl.CreateTrackCommand;
import com.codeconnect.curriculum.application.dto.request.CreateLessonRequest;
import com.codeconnect.curriculum.application.dto.request.CreateModuleRequest;
import com.codeconnect.curriculum.application.dto.request.CreateTrackRequest;
import com.codeconnect.curriculum.application.mapper.CurriculumMapper;
import com.codeconnect.curriculum.application.service.collaborator.track.ModuleSummaryUpdater;
import com.codeconnect.curriculum.application.validator.CurriculumValidator;
import com.codeconnect.curriculum.domain.repository.LessonRepository;
import com.codeconnect.curriculum.domain.repository.ModuleRepository;
import com.codeconnect.curriculum.domain.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Factory component assembling executable CurriculumCommand instances with required dependencies.
 * Adheres to Factory Pattern (GoF) and Dependency Inversion Principle (DIP).
 */
@Component
@RequiredArgsConstructor
public class CurriculumCommandFactory {

    private final TrackRepository trackRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final CurriculumValidator validator;
    private final CurriculumMapper mapper;
    private final ModuleSummaryUpdater moduleSummaryUpdater;
    private final ApplicationEventPublisher eventPublisher;

    public CreateTrackCommand createTrackCommand(CreateTrackRequest request) {
        return new CreateTrackCommand(request, trackRepository, validator, mapper, eventPublisher);
    }

    public CreateModuleCommand createModuleCommand(CreateModuleRequest request) {
        return new CreateModuleCommand(request, moduleRepository, trackRepository, validator, mapper, moduleSummaryUpdater);
    }

    public CreateLessonCommand createLessonCommand(CreateLessonRequest request) {
        return new CreateLessonCommand(request, lessonRepository, validator, mapper, moduleSummaryUpdater, eventPublisher);
    }
}
