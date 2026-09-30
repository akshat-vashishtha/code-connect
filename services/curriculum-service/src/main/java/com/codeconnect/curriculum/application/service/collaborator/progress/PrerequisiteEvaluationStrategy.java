package com.codeconnect.curriculum.application.service.collaborator.progress;

import com.codeconnect.curriculum.application.dto.response.PrerequisiteRecommendationResponse;
import com.codeconnect.curriculum.domain.model.LessonDocument;
import com.codeconnect.curriculum.domain.model.StudentProgressDocument;

/**
 * Strategy contract for evaluating prerequisite lesson recommendations.
 * Implementations encapsulate distinct recommendation rules (completed, uncompleted, optional).
 * Follows the Open/Closed Principle: new prerequisite evaluation behaviours are added
 * as new strategy implementations without modifying existing classes.
 */
public interface PrerequisiteEvaluationStrategy {

    /**
     * Returns true when this strategy is applicable for the given lesson and progress state.
     */
    boolean supports(LessonDocument lesson, StudentProgressDocument progress);

    /**
     * Evaluates the prerequisite and produces a recommendation response.
     */
    PrerequisiteRecommendationResponse evaluate(LessonDocument lesson, StudentProgressDocument progress);
}
