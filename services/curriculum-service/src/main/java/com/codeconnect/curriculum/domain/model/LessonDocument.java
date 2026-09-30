package com.codeconnect.curriculum.domain.model;

import com.codeconnect.curriculum.domain.enums.LanguageMode;
import com.codeconnect.curriculum.domain.valueobject.StoryContent;
import com.codeconnect.curriculum.domain.valueobject.TestCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Domain entity representing a learning lesson (foothold) within a curriculum module.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "lessons")
public class LessonDocument {

    @Id
    private String id;
    private String moduleId;
    private String trackId;
    private String title;
    private String slug;
    private Integer sequence;

    @Builder.Default
    private Map<LanguageMode, StoryContent> storyAnalogies = new HashMap<>();

    private String starterCode;
    private String solutionTemplate;

    @Builder.Default
    private List<TestCase> testCases = new ArrayList<>();

    private String prerequisiteLessonId;
    private Instant createdAt;
}

