package com.codeconnect.curriculum.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Rich domain Aggregate Root representing a student's learning progress within a track.
 * Encapsulates completion invariants, ascent points accumulation, and streaks.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "student_progress")
public class StudentProgressDocument {

    @Id
    private String id;
    private String userId;
    private String trackId;
    private String currentModuleId;
    private String currentLessonId;

    @Builder.Default
    private Set<String> completedLessonIds = new HashSet<>();

    @Builder.Default
    private Integer ascentPoints = 0;

    @Builder.Default
    private Integer streakDays = 0;

    private Instant lastCompletedAt;

    /**
     * Records completion of a lesson, awards points, and updates state invariants.
     */
    public void completeLesson(String lessonId, int pointsEarned) {
        if (this.completedLessonIds == null) {
            this.completedLessonIds = new HashSet<>();
        }
        this.completedLessonIds.add(lessonId);
        this.currentLessonId = lessonId;
        this.ascentPoints = (this.ascentPoints != null ? this.ascentPoints : 0) + pointsEarned;
        this.lastCompletedAt = Instant.now();
    }

    /**
     * Checks if a lesson has already been completed by the student.
     */
    public boolean hasCompletedLesson(String lessonId) {
        return this.completedLessonIds != null && this.completedLessonIds.contains(lessonId);
    }
}

