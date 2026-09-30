package com.codeconnect.curriculum.application.dto.request;

import com.codeconnect.curriculum.domain.enums.LanguageMode;
import com.codeconnect.curriculum.domain.valueobject.StoryContent;
import com.codeconnect.curriculum.domain.valueobject.TestCase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.Map;

public record CreateLessonRequest(
    @NotBlank(message = "Module ID is required")
    String moduleId,

    @NotBlank(message = "Track ID is required")
    String trackId,

    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "Slug is required")
    String slug,

    @NotNull(message = "Sequence is required")
    @Positive(message = "Sequence must be positive")
    Integer sequence,

    @NotNull(message = "Story analogies are required")
    Map<LanguageMode, StoryContent> storyAnalogies,

    @NotBlank(message = "Starter code is required")
    String starterCode,

    @NotBlank(message = "Solution template is required")
    String solutionTemplate,

    List<TestCase> testCases,

    String prerequisiteLessonId
) {}
