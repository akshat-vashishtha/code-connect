package com.codeconnect.curriculum.application.service.impl;

import com.codeconnect.curriculum.application.dto.*;
import com.codeconnect.curriculum.application.mapper.CurriculumMapper;
import com.codeconnect.curriculum.application.service.AdminCurriculumService;
import com.codeconnect.curriculum.application.validator.CurriculumValidator;
import com.codeconnect.curriculum.domain.model.LessonDocument;
import com.codeconnect.curriculum.domain.model.ModuleDocument;
import com.codeconnect.curriculum.domain.model.ModuleSummary;
import com.codeconnect.curriculum.domain.model.TrackDocument;
import com.codeconnect.curriculum.domain.repository.LessonRepository;
import com.codeconnect.curriculum.domain.repository.ModuleRepository;
import com.codeconnect.curriculum.domain.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AdminCurriculumServiceImpl implements AdminCurriculumService {

    private final TrackRepository trackRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final CurriculumMapper curriculumMapper;
    private final CurriculumValidator curriculumValidator;

    @Override
    @Transactional
    public TrackResponse createTrack(CreateTrackRequest request) {
        curriculumValidator.validateTrackCreation(request);
        TrackDocument doc = curriculumMapper.toTrackDocument(request);
        TrackDocument savedDoc = trackRepository.save(doc);
        return curriculumMapper.toTrackResponse(savedDoc);
    }

    @Override
    @Transactional
    public ModuleResponse createModule(CreateModuleRequest request) {
        curriculumValidator.validateModuleCreation(request);
        ModuleDocument doc = curriculumMapper.toModuleDocument(request);
        ModuleDocument savedDoc = moduleRepository.save(doc);

        TrackDocument track = trackRepository.findById(request.trackId()).orElseThrow();
        ModuleSummary summary = ModuleSummary.builder()
            .id(savedDoc.getId())
            .title(savedDoc.getTitle())
            .slug(savedDoc.getSlug())
            .sequence(savedDoc.getSequence())
            .lessonCount(0)
            .build();

        track.getModules().add(summary);
        trackRepository.save(track);

        return curriculumMapper.toModuleResponse(savedDoc, Collections.emptyList());
    }

    @Override
    @Transactional
    public LessonResponse createLesson(CreateLessonRequest request) {
        curriculumValidator.validateLessonCreation(request);
        LessonDocument doc = curriculumMapper.toLessonDocument(request);
        LessonDocument savedDoc = lessonRepository.save(doc);

        moduleRepository.findById(request.moduleId()).ifPresent(module -> {
            TrackDocument track = trackRepository.findById(request.trackId()).orElse(null);
            if (track != null && track.getModules() != null) {
                int count = (int) lessonRepository.countByModuleId(request.moduleId());
                track.getModules().stream()
                    .filter(m -> m.getId().equals(module.getId()))
                    .findFirst()
                    .ifPresent(m -> m.setLessonCount(count));
                trackRepository.save(track);
            }
        });

        return curriculumMapper.toLessonResponse(savedDoc, null);
    }

    @Override
    public LessonResponse getLessonById(String lessonId) {
        LessonDocument doc = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new com.codeconnect.curriculum.domain.exception.ResourceNotFoundException("Lesson not found with id: " + lessonId));
        return curriculumMapper.toLessonResponse(doc, null);
    }
}
