package com.codeconnect.curriculum.application.service.impl;

import com.codeconnect.curriculum.application.dto.*;
import com.codeconnect.curriculum.application.mapper.CurriculumMapper;
import com.codeconnect.curriculum.application.service.CurriculumService;
import com.codeconnect.curriculum.domain.enums.TrackStatus;
import com.codeconnect.curriculum.domain.exception.ResourceNotFoundException;
import com.codeconnect.curriculum.domain.model.LessonDocument;
import com.codeconnect.curriculum.domain.model.StudentProgressDocument;
import com.codeconnect.curriculum.domain.repository.LessonRepository;
import com.codeconnect.curriculum.domain.repository.ModuleRepository;
import com.codeconnect.curriculum.domain.repository.StudentProgressRepository;
import com.codeconnect.curriculum.domain.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CurriculumServiceImpl implements CurriculumService {

    private final TrackRepository trackRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final StudentProgressRepository progressRepository;
    private final CurriculumMapper curriculumMapper;

    @Override
    public List<TrackResponse> getPublishedTracks() {
        return trackRepository.findByStatus(TrackStatus.PUBLISHED).stream()
            .map(curriculumMapper::toTrackResponse)
            .toList();
    }

    @Override
    public TrackResponse getTrackById(String trackId) {
        return trackRepository.findById(trackId)
            .map(curriculumMapper::toTrackResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Track not found with ID: " + trackId));
    }

    @Override
    public List<ModuleResponse> getModulesByTrackId(String trackId) {
        if (!trackRepository.existsById(trackId)) {
            throw new ResourceNotFoundException("Track not found with ID: " + trackId);
        }

        return moduleRepository.findByTrackIdOrderBySequenceAsc(trackId).stream()
            .map(module -> {
                List<LessonResponse> lessons = lessonRepository.findByModuleIdOrderBySequenceAsc(module.getId())
                    .stream()
                    .map(lesson -> curriculumMapper.toPublicLessonResponse(lesson, null))
                    .toList();
                return curriculumMapper.toModuleResponse(module, lessons);
            })
            .toList();
    }

    @Override
    public LessonResponse getLessonById(String lessonId, String userId) {
        LessonDocument lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with ID: " + lessonId));

        PrerequisiteRecommendationResponse prereqRecommendation = null;
        if (lesson.getPrerequisiteLessonId() != null && !lesson.getPrerequisiteLessonId().isBlank()) {
            prereqRecommendation = calculatePrerequisiteRecommendation(lesson.getPrerequisiteLessonId(), lesson.getTrackId(), userId);
        }

        return curriculumMapper.toPublicLessonResponse(lesson, prereqRecommendation);
    }

    @Override
    public StudentProgressResponse getStudentProgress(String userId, String trackId) {
        StudentProgressDocument progress = progressRepository.findByUserIdAndTrackId(userId, trackId)
            .orElseGet(() -> StudentProgressDocument.builder()
                .userId(userId)
                .trackId(trackId)
                .completedLessonIds(Collections.emptySet())
                .ascentPoints(0)
                .streakDays(0)
                .build());

        return curriculumMapper.toProgressResponse(progress);
    }

    private PrerequisiteRecommendationResponse calculatePrerequisiteRecommendation(String prereqLessonId, String trackId, String userId) {
        String prereqTitle = lessonRepository.findById(prereqLessonId)
            .map(LessonDocument::getTitle)
            .orElse("Prerequisite Lesson");

        boolean isCompleted = false;
        if (userId != null && !userId.isBlank()) {
            isCompleted = progressRepository.findByUserIdAndTrackId(userId, trackId)
                .map(p -> p.getCompletedLessonIds() != null && p.getCompletedLessonIds().contains(prereqLessonId))
                .orElse(false);
        }

        boolean isRecommended = !isCompleted;
        String badgeText = isRecommended ? "Prerequisite " + prereqTitle + " Recommended" : "Prerequisite Completed";

        return new PrerequisiteRecommendationResponse(
            prereqLessonId,
            prereqTitle,
            isCompleted,
            isRecommended,
            badgeText
        );
    }
}
