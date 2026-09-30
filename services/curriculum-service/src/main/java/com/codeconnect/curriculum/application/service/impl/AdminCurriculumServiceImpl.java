package com.codeconnect.curriculum.application.service.impl;

import com.codeconnect.curriculum.application.command.factory.CurriculumCommandFactory;
import com.codeconnect.curriculum.application.dto.request.*;
import com.codeconnect.curriculum.application.dto.response.*;
import com.codeconnect.curriculum.application.mapper.CurriculumMapper;
import com.codeconnect.curriculum.application.service.AdminCurriculumService;
import com.codeconnect.curriculum.domain.exception.ResourceNotFoundException;
import com.codeconnect.curriculum.domain.model.LessonDocument;
import com.codeconnect.curriculum.domain.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service facade orchestrating curriculum administrative workflows by dispatching Command objects.
 * Follows the Command Pattern (GoF) and Facade Pattern (GoF).
 */
@Service
@RequiredArgsConstructor
public class AdminCurriculumServiceImpl implements AdminCurriculumService {

    private final CurriculumCommandFactory commandFactory;
    private final LessonRepository lessonRepository;
    private final CurriculumMapper curriculumMapper;

    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "tracks", allEntries = true)
    public TrackResponse createTrack(CreateTrackRequest request) {
        return commandFactory.createTrackCommand(request).execute();
    }

    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = {"tracks", "modules"}, allEntries = true)
    public ModuleResponse createModule(CreateModuleRequest request) {
        return commandFactory.createModuleCommand(request).execute();
    }

    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = {"modules", "lessons"}, allEntries = true)
    public LessonResponse createLesson(CreateLessonRequest request) {
        return commandFactory.createLessonCommand(request).execute();
    }

    @Override
    @org.springframework.cache.annotation.Cacheable(value = "lessons", key = "#lessonId", sync = true)
    public LessonResponse getLessonById(String lessonId) {
        LessonDocument doc = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));
        return curriculumMapper.toLessonResponse(doc, null);
    }
}

