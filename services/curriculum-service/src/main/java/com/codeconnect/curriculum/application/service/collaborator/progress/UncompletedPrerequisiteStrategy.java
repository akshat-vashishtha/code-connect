package com.codeconnect.curriculum.application.service.collaborator.progress;

import com.codeconnect.curriculum.application.dto.response.PrerequisiteRecommendationResponse;
import com.codeconnect.curriculum.domain.model.LessonDocument;
import com.codeconnect.curriculum.domain.model.StudentProgressDocument;
import com.codeconnect.curriculum.domain.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Strategy applied when the student has NOT yet completed the prerequisite lesson.
 * Resolves the prerequisite title from the repository and produces a recommendation response.
 */
@Component
@Order(2)
@RequiredArgsConstructor
public class UncompletedPrerequisiteStrategy implements PrerequisiteEvaluationStrategy {

    private final LessonRepository lessonRepository;

    @Override
    public boolean supports(LessonDocument lesson, StudentProgressDocument progress) {
        String prereqId = lesson.getPrerequisiteLessonId();
        return prereqId != null && !prereqId.isBlank();
    }

    @Override
    public PrerequisiteRecommendationResponse evaluate(LessonDocument lesson, StudentProgressDocument progress) {
        String prereqId = lesson.getPrerequisiteLessonId();
        String prereqTitle = resolvePrerequisiteTitle(prereqId);
        return new PrerequisiteRecommendationResponse(
            prereqId,
            prereqTitle,
            false,
            true,
            "Prerequisite " + prereqTitle + " Recommended"
        );
    }

    private String resolvePrerequisiteTitle(String prereqId) {
        return lessonRepository.findById(prereqId)
            .map(LessonDocument::getTitle)
            .orElse("Prerequisite Lesson");
    }
}
