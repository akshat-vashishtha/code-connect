package com.codeconnect.curriculum.application.dto.response;

import com.codeconnect.curriculum.domain.enums.LanguageMode;
import com.codeconnect.curriculum.domain.valueobject.StoryContent;
import com.codeconnect.curriculum.domain.valueobject.TestCase;

import java.util.List;
import java.util.Map;

public record LessonResponse(
    String id,
    String moduleId,
    String trackId,
    String title,
    String slug,
    Integer sequence,
    Map<LanguageMode, StoryContent> storyAnalogies,
    String starterCode,
    String solutionTemplate,
    List<TestCase> testCases,
    String prerequisiteLessonId,
    PrerequisiteRecommendationResponse prerequisiteRecommendation
) {}
