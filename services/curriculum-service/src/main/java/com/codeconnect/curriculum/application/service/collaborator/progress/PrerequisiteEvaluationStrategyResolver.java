package com.codeconnect.curriculum.application.service.collaborator.progress;

import com.codeconnect.curriculum.application.dto.response.PrerequisiteRecommendationResponse;
import com.codeconnect.curriculum.domain.model.LessonDocument;
import com.codeconnect.curriculum.domain.model.StudentProgressDocument;
import com.codeconnect.curriculum.domain.repository.StudentProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Resolver that selects the appropriate PrerequisiteEvaluationStrategy for a given lesson and progress context.
 * Applies the ordered list of strategies, delegating to the first that declares support.
 * Acts as the Strategy Runner (Registry pattern) injecting all available strategy implementations.
 */
@Component
@RequiredArgsConstructor
public class PrerequisiteEvaluationStrategyResolver {

    private final List<PrerequisiteEvaluationStrategy> strategies;
    private final StudentProgressRepository progressRepository;

    public Optional<PrerequisiteRecommendationResponse> resolve(LessonDocument lesson, String userId) {
        if (lesson.getPrerequisiteLessonId() == null || lesson.getPrerequisiteLessonId().isBlank()) {
            return Optional.empty();
        }

        StudentProgressDocument progress = resolveProgress(lesson.getTrackId(), userId);

        return strategies.stream()
            .filter(strategy -> strategy.supports(lesson, progress))
            .findFirst()
            .map(strategy -> strategy.evaluate(lesson, progress));
    }

    private StudentProgressDocument resolveProgress(String trackId, String userId) {
        if (userId == null || userId.isBlank()) {
            return null;
        }
        return progressRepository.findByUserIdAndTrackId(userId, trackId).orElse(null);
    }
}
