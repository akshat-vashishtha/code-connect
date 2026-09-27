package com.codeconnect.curriculum.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "student_progress")
public class StudentProgressDocument {
    @Id
    private String id;
    private String userId;
    private String trackId;
    private String currentModuleId;
    private String currentLessonId;
    private Set<String> completedLessonIds = new HashSet<>();
    private Integer ascentPoints;
    private Integer streakDays;
    private Instant lastCompletedAt;

    public StudentProgressDocument() {}

    public StudentProgressDocument(String id, String userId, String trackId, String currentModuleId, String currentLessonId, Set<String> completedLessonIds, Integer ascentPoints, Integer streakDays, Instant lastCompletedAt) {
        this.id = id;
        this.userId = userId;
        this.trackId = trackId;
        this.currentModuleId = currentModuleId;
        this.currentLessonId = currentLessonId;
        this.completedLessonIds = completedLessonIds != null ? completedLessonIds : new HashSet<>();
        this.ascentPoints = ascentPoints;
        this.streakDays = streakDays;
        this.lastCompletedAt = lastCompletedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTrackId() { return trackId; }
    public void setTrackId(String trackId) { this.trackId = trackId; }
    public String getCurrentModuleId() { return currentModuleId; }
    public void setCurrentModuleId(String currentModuleId) { this.currentModuleId = currentModuleId; }
    public String getCurrentLessonId() { return currentLessonId; }
    public void setCurrentLessonId(String currentLessonId) { this.currentLessonId = currentLessonId; }
    public Set<String> getCompletedLessonIds() { return completedLessonIds; }
    public void setCompletedLessonIds(Set<String> completedLessonIds) { this.completedLessonIds = completedLessonIds; }
    public Integer getAscentPoints() { return ascentPoints; }
    public void setAscentPoints(Integer ascentPoints) { this.ascentPoints = ascentPoints; }
    public Integer getStreakDays() { return streakDays; }
    public void setStreakDays(Integer streakDays) { this.streakDays = streakDays; }
    public Instant getLastCompletedAt() { return lastCompletedAt; }
    public void setLastCompletedAt(Instant lastCompletedAt) { this.lastCompletedAt = lastCompletedAt; }

    public static StudentProgressDocumentBuilder builder() {
        return new StudentProgressDocumentBuilder();
    }

    public static class StudentProgressDocumentBuilder {
        private String id;
        private String userId;
        private String trackId;
        private String currentModuleId;
        private String currentLessonId;
        private Set<String> completedLessonIds = new HashSet<>();
        private Integer ascentPoints;
        private Integer streakDays;
        private Instant lastCompletedAt;

        public StudentProgressDocumentBuilder id(String id) { this.id = id; return this; }
        public StudentProgressDocumentBuilder userId(String userId) { this.userId = userId; return this; }
        public StudentProgressDocumentBuilder trackId(String trackId) { this.trackId = trackId; return this; }
        public StudentProgressDocumentBuilder currentModuleId(String currentModuleId) { this.currentModuleId = currentModuleId; return this; }
        public StudentProgressDocumentBuilder currentLessonId(String currentLessonId) { this.currentLessonId = currentLessonId; return this; }
        public StudentProgressDocumentBuilder completedLessonIds(Set<String> completedLessonIds) { this.completedLessonIds = completedLessonIds; return this; }
        public StudentProgressDocumentBuilder ascentPoints(Integer ascentPoints) { this.ascentPoints = ascentPoints; return this; }
        public StudentProgressDocumentBuilder streakDays(Integer streakDays) { this.streakDays = streakDays; return this; }
        public StudentProgressDocumentBuilder lastCompletedAt(Instant lastCompletedAt) { this.lastCompletedAt = lastCompletedAt; return this; }

        public StudentProgressDocument build() {
            return new StudentProgressDocument(id, userId, trackId, currentModuleId, currentLessonId, completedLessonIds, ascentPoints, streakDays, lastCompletedAt);
        }
    }
}
