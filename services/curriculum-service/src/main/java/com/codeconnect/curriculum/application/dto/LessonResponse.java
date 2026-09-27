package com.codeconnect.curriculum.application.dto;

import com.codeconnect.curriculum.domain.enums.LanguageMode;
import com.codeconnect.curriculum.domain.model.StoryContent;
import com.codeconnect.curriculum.domain.model.TestCase;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
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
