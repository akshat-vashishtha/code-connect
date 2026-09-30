package com.codeconnect.curriculum.application.service.impl;

import com.codeconnect.curriculum.application.dto.response.*;
import com.codeconnect.curriculum.application.mapper.CurriculumMapper;
import com.codeconnect.curriculum.application.service.CurriculumService;
import com.codeconnect.curriculum.application.service.collaborator.progress.PrerequisiteEvaluationStrategyResolver;
import com.codeconnect.curriculum.domain.enums.TrackStatus;
import com.codeconnect.curriculum.domain.enums.UserRole;
import com.codeconnect.curriculum.domain.exception.CurriculumValidationException;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Application service facade orchestrating curriculum read workflows.
 * Adheres strictly to SLAP and SRP by delegating:
 * - Prerequisite evaluation to PrerequisiteEvaluationStrategyResolver (Strategy Pattern)
 * - Security context inspection to SecurityContextAccessor (DIP)
 */
@Service
@RequiredArgsConstructor
public class CurriculumServiceImpl implements CurriculumService {

    private final TrackRepository trackRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final StudentProgressRepository progressRepository;
    private final CurriculumMapper curriculumMapper;
    private final PrerequisiteEvaluationStrategyResolver prerequisiteResolver;
    private final SecurityContextAccessor securityContextAccessor;

    @Override
    @org.springframework.cache.annotation.Cacheable(value = "tracks", key = "'all-published'", sync = true)
    public List<TrackResponse> getPublishedTracks() {
        return trackRepository.findByStatus(TrackStatus.PUBLISHED).stream()
            .map(curriculumMapper::toTrackResponse)
            .toList();
    }

    @Override
    @org.springframework.cache.annotation.Cacheable(value = "tracks", key = "#trackId", sync = true)
    public TrackResponse getTrackById(String trackId) {
        return trackRepository.findById(trackId)
            .map(curriculumMapper::toTrackResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Track not found with ID: " + trackId));
    }

    @Override
    @org.springframework.cache.annotation.Cacheable(value = "modules", key = "#trackId", sync = true)
    public List<ModuleResponse> getModulesByTrackId(String trackId) {
        if (!trackRepository.existsById(trackId)) {
            throw new ResourceNotFoundException("Track not found with ID: " + trackId);
        }
        List<ModuleDocument> modules = moduleRepository.findByTrackIdOrderBySequenceAsc(trackId);
        if (modules.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> moduleIds = modules.stream().map(ModuleDocument::getId).toList();
        Map<String, List<LessonDocument>> lessonsByModuleId = lessonRepository.findByModuleIdIn(moduleIds).stream()
            .collect(Collectors.groupingBy(LessonDocument::getModuleId));

        return modules.stream()
            .map(module -> {
                List<LessonResponse> lessons = lessonsByModuleId.getOrDefault(module.getId(), Collections.emptyList()).stream()
                    .sorted(Comparator.comparing(LessonDocument::getSequence, Comparator.nullsLast(Integer::compareTo)))
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

        PrerequisiteRecommendationResponse prereqRecommendation = prerequisiteResolver
            .resolve(lesson, userId)
            .orElse(null);

        return curriculumMapper.toPublicLessonResponse(lesson, prereqRecommendation);
    }

    @Override
    @org.springframework.cache.annotation.Cacheable(value = "student_progress", key = "#userId + ':' + #trackId")
    public StudentProgressResponse getStudentProgress(String userId, String trackId) {
        assertProgressAccessAuthorized(userId);

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

    private void assertProgressAccessAuthorized(String userId) {
        if (!securityContextAccessor.isAuthenticated()) {
            return;
        }
        boolean isStaff = securityContextAccessor.hasRole(UserRole.ADMIN)
            || securityContextAccessor.hasRole(UserRole.MENTOR);
        boolean isSelf = securityContextAccessor.resolveAuthenticatedUserId()
            .map(id -> id.equals(userId))
            .orElse(false);

        if (!isStaff && !isSelf) {
            throw new CurriculumValidationException("Access Denied: Cannot access progress records of another user");
        }
    }
}
