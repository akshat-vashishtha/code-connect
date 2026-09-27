package com.codeconnect.curriculum.application.dto;

import com.codeconnect.curriculum.domain.enums.LanguageMode;
import com.codeconnect.curriculum.domain.model.StoryContent;
import com.codeconnect.curriculum.domain.model.TestCase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

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
    Integer sequence,

    @NotEmpty(message = "Story analogies are required")
    Map<LanguageMode, StoryContent> storyAnalogies,

    @NotBlank(message = "Starter code is required")
    String starterCode,

    String solutionTemplate,

    List<TestCase> testCases,

    String prerequisiteLessonId
) {}
