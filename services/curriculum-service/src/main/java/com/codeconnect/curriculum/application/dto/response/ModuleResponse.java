package com.codeconnect.curriculum.application.dto.response;

import java.util.List;

public record ModuleResponse(
    String id,
    String trackId,
    String title,
    String slug,
    Integer sequence,
    String description,
    String prerequisiteModuleId,
    List<LessonResponse> lessons
) {}
