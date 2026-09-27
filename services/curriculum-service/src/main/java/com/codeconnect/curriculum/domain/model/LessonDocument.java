package com.codeconnect.curriculum.domain.model;

import com.codeconnect.curriculum.domain.enums.LanguageMode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "lessons")
public class LessonDocument {
    @Id
    private String id;
    private String moduleId;
    private String trackId;
    private String title;
    private String slug;
    private Integer sequence;
    private Map<LanguageMode, StoryContent> storyAnalogies = new HashMap<>();
    private String starterCode;
    private String solutionTemplate;
    private List<TestCase> testCases = new ArrayList<>();
    private String prerequisiteLessonId;
    private Instant createdAt;

    public LessonDocument() {}

    public LessonDocument(String id, String moduleId, String trackId, String title, String slug, Integer sequence, Map<LanguageMode, StoryContent> storyAnalogies, String starterCode, String solutionTemplate, List<TestCase> testCases, String prerequisiteLessonId, Instant createdAt) {
        this.id = id;
        this.moduleId = moduleId;
        this.trackId = trackId;
        this.title = title;
        this.slug = slug;
        this.sequence = sequence;
        this.storyAnalogies = storyAnalogies != null ? storyAnalogies : new HashMap<>();
        this.starterCode = starterCode;
        this.solutionTemplate = solutionTemplate;
        this.testCases = testCases != null ? testCases : new ArrayList<>();
        this.prerequisiteLessonId = prerequisiteLessonId;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getModuleId() { return moduleId; }
    public void setModuleId(String moduleId) { this.moduleId = moduleId; }
    public String getTrackId() { return trackId; }
    public void setTrackId(String trackId) { this.trackId = trackId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public Integer getSequence() { return sequence; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public Map<LanguageMode, StoryContent> getStoryAnalogies() { return storyAnalogies; }
    public void setStoryAnalogies(Map<LanguageMode, StoryContent> storyAnalogies) { this.storyAnalogies = storyAnalogies; }
    public String getStarterCode() { return starterCode; }
    public void setStarterCode(String starterCode) { this.starterCode = starterCode; }
    public String getSolutionTemplate() { return solutionTemplate; }
    public void setSolutionTemplate(String solutionTemplate) { this.solutionTemplate = solutionTemplate; }
    public List<TestCase> getTestCases() { return testCases; }
    public void setTestCases(List<TestCase> testCases) { this.testCases = testCases; }
    public String getPrerequisiteLessonId() { return prerequisiteLessonId; }
    public void setPrerequisiteLessonId(String prerequisiteLessonId) { this.prerequisiteLessonId = prerequisiteLessonId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static LessonDocumentBuilder builder() {
        return new LessonDocumentBuilder();
    }

    public static class LessonDocumentBuilder {
        private String id;
        private String moduleId;
        private String trackId;
        private String title;
        private String slug;
        private Integer sequence;
        private Map<LanguageMode, StoryContent> storyAnalogies = new HashMap<>();
        private String starterCode;
        private String solutionTemplate;
        private List<TestCase> testCases = new ArrayList<>();
        private String prerequisiteLessonId;
        private Instant createdAt;

        public LessonDocumentBuilder id(String id) { this.id = id; return this; }
        public LessonDocumentBuilder moduleId(String moduleId) { this.moduleId = moduleId; return this; }
        public LessonDocumentBuilder trackId(String trackId) { this.trackId = trackId; return this; }
        public LessonDocumentBuilder title(String title) { this.title = title; return this; }
        public LessonDocumentBuilder slug(String slug) { this.slug = slug; return this; }
        public LessonDocumentBuilder sequence(Integer sequence) { this.sequence = sequence; return this; }
        public LessonDocumentBuilder storyAnalogies(Map<LanguageMode, StoryContent> storyAnalogies) { this.storyAnalogies = storyAnalogies; return this; }
        public LessonDocumentBuilder starterCode(String starterCode) { this.starterCode = starterCode; return this; }
        public LessonDocumentBuilder solutionTemplate(String solutionTemplate) { this.solutionTemplate = solutionTemplate; return this; }
        public LessonDocumentBuilder testCases(List<TestCase> testCases) { this.testCases = testCases; return this; }
        public LessonDocumentBuilder prerequisiteLessonId(String prerequisiteLessonId) { this.prerequisiteLessonId = prerequisiteLessonId; return this; }
        public LessonDocumentBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public LessonDocument build() {
            return new LessonDocument(id, moduleId, trackId, title, slug, sequence, storyAnalogies, starterCode, solutionTemplate, testCases, prerequisiteLessonId, createdAt);
        }
    }
}
