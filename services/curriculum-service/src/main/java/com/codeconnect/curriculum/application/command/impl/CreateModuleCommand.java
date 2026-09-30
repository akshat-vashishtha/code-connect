package com.codeconnect.curriculum.application.command.impl;

import com.codeconnect.curriculum.application.command.CurriculumCommand;
import com.codeconnect.curriculum.application.dto.request.CreateModuleRequest;
import com.codeconnect.curriculum.application.dto.response.ModuleResponse;
import com.codeconnect.curriculum.application.mapper.CurriculumMapper;
import com.codeconnect.curriculum.application.service.collaborator.track.ModuleSummaryUpdater;
import com.codeconnect.curriculum.application.validator.CurriculumValidator;
import com.codeconnect.curriculum.domain.exception.ResourceNotFoundException;
import com.codeconnect.curriculum.domain.model.ModuleDocument;
import com.codeconnect.curriculum.domain.valueobject.ModuleSummary;
import com.codeconnect.curriculum.domain.model.TrackDocument;
import com.codeconnect.curriculum.domain.repository.ModuleRepository;
import com.codeconnect.curriculum.domain.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

/**
 * Command encapsulating Module creation and Track summary synchronization workflow.
 * Follows the Command Pattern (GoF).
 */
@Slf4j
@RequiredArgsConstructor
public class CreateModuleCommand implements CurriculumCommand<ModuleResponse> {

    private final CreateModuleRequest request;
    private final ModuleRepository moduleRepository;
    private final TrackRepository trackRepository;
    private final CurriculumValidator validator;
    private final CurriculumMapper mapper;
    private final ModuleSummaryUpdater moduleSummaryUpdater;

    @Override
    public ModuleResponse execute() {
        log.info("Executing CreateModuleCommand for trackId={} slug={}", request.trackId(), request.slug());
        validator.validateModuleCreation(request);
        ModuleDocument savedModule = moduleRepository.save(mapper.toModuleDocument(request));

        TrackDocument track = trackRepository.findById(request.trackId())
            .orElseThrow(() -> new ResourceNotFoundException("Track not found with ID: " + request.trackId()));

        ModuleSummary summary = ModuleSummary.builder()
            .id(savedModule.getId())
            .title(savedModule.getTitle())
            .slug(savedModule.getSlug())
            .sequence(savedModule.getSequence())
            .lessonCount(0)
            .build();

        moduleSummaryUpdater.appendModuleSummary(track, summary);

        return mapper.toModuleResponse(savedModule, Collections.emptyList());
    }
}
