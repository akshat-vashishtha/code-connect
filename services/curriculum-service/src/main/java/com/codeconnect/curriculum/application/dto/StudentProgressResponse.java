package com.codeconnect.curriculum.application.dto;

import java.time.Instant;
import java.util.Set;

public record StudentProgressResponse(
    String id,
    String userId,
    String trackId,
    String currentModuleId,
    String currentLessonId,
    Set<String> completedLessonIds,
    Integer ascentPoints,
    Integer streakDays,
    Instant lastCompletedAt
) {}
