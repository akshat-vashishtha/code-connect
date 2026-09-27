package com.codeconnect.curriculum.application.dto;

public record PrerequisiteRecommendationResponse(
    String prerequisiteLessonId,
    String prerequisiteLessonTitle,
    boolean isCompleted,
    boolean isRecommended,
    String recommendationBadgeText
) {}
