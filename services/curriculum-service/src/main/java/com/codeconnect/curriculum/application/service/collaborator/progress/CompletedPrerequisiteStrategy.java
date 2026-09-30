package com.codeconnect.curriculum.application.service.collaborator.progress;

import com.codeconnect.curriculum.application.dto.response.PrerequisiteRecommendationResponse;
import com.codeconnect.curriculum.domain.model.LessonDocument;
import com.codeconnect.curriculum.domain.model.StudentProgressDocument;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Strategy applied when the student has already completed the prerequisite lesson.
 * Produces a "Prerequisite Completed" affirmation response.
 */
@Component
@Order(1)
public class CompletedPrerequisiteStrategy implements PrerequisiteEvaluationStrategy {

    @Override
    public boolean supports(LessonDocument lesson, StudentProgressDocument progress) {
        String prereqId = lesson.getPrerequisiteLessonId();
        if (prereqId == null || prereqId.isBlank() || progress == null) {
            return false;
        }
        return progress.getCompletedLessonIds() != null
            && progress.getCompletedLessonIds().contains(prereqId);
    }

    @Override
    public PrerequisiteRecommendationResponse evaluate(LessonDocument lesson, StudentProgressDocument progress) {
        return new PrerequisiteRecommendationResponse(
            lesson.getPrerequisiteLessonId(),
            null,
            true,
            false,
            "Prerequisite Completed"
        );
    }
}

