package com.codeconnect.curriculum.application.mapper;

import com.codeconnect.curriculum.application.dto.*;
import com.codeconnect.curriculum.domain.model.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Component
public class CurriculumMapper {

    public TrackResponse toTrackResponse(TrackDocument doc) {
        if (doc == null) return null;
        return new TrackResponse(
            doc.getId(),
            doc.getTitle(),
            doc.getSlug(),
            doc.getDescription(),
            doc.getEstimatedHours(),
            doc.getStatus(),
            doc.getModules() != null ? doc.getModules() : Collections.emptyList(),
            doc.getCreatedAt()
        );
    }

    public ModuleResponse toModuleResponse(ModuleDocument doc, List<LessonResponse> lessons) {
        if (doc == null) return null;
        return new ModuleResponse(
            doc.getId(),
            doc.getTrackId(),
            doc.getTitle(),
            doc.getSlug(),
            doc.getSequence(),
            doc.getDescription(),
            doc.getPrerequisiteModuleId(),
            lessons != null ? lessons : Collections.emptyList()
        );
    }

    public LessonResponse toLessonResponse(LessonDocument doc, PrerequisiteRecommendationResponse prereqRecommendation) {
        if (doc == null) return null;
        return new LessonResponse(
            doc.getId(),
            doc.getModuleId(),
            doc.getTrackId(),
            doc.getTitle(),
            doc.getSlug(),
            doc.getSequence(),
            doc.getStoryAnalogies(),
            doc.getStarterCode(),
            doc.getSolutionTemplate(),
            doc.getTestCases(),
            doc.getPrerequisiteLessonId(),
            prereqRecommendation
        );
    }

    public LessonResponse toPublicLessonResponse(LessonDocument doc, PrerequisiteRecommendationResponse prereqRecommendation) {
        if (doc == null) return null;
        return new LessonResponse(
            doc.getId(),
            doc.getModuleId(),
            doc.getTrackId(),
            doc.getTitle(),
            doc.getSlug(),
            doc.getSequence(),
            doc.getStoryAnalogies(),
            doc.getStarterCode(),
            null,
            doc.getTestCases(),
            doc.getPrerequisiteLessonId(),
            prereqRecommendation
        );
    }

    public StudentProgressResponse toProgressResponse(StudentProgressDocument doc) {
        if (doc == null) return null;
        return new StudentProgressResponse(
            doc.getId(),
            doc.getUserId(),
            doc.getTrackId(),
            doc.getCurrentModuleId(),
            doc.getCurrentLessonId(),
            doc.getCompletedLessonIds() != null ? doc.getCompletedLessonIds() : Collections.emptySet(),
            doc.getAscentPoints(),
            doc.getStreakDays(),
            doc.getLastCompletedAt()
        );
    }

    public TrackDocument toTrackDocument(CreateTrackRequest req) {
        return TrackDocument.builder()
            .title(req.title())
            .slug(req.slug())
            .description(req.description())
            .estimatedHours(req.estimatedHours())
            .status(req.status())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }

    public ModuleDocument toModuleDocument(CreateModuleRequest req) {
        return ModuleDocument.builder()
            .trackId(req.trackId())
            .title(req.title())
            .slug(req.slug())
            .sequence(req.sequence())
            .description(req.description())
            .prerequisiteModuleId(req.prerequisiteModuleId())
            .createdAt(Instant.now())
            .build();
    }

    public LessonDocument toLessonDocument(CreateLessonRequest req) {
        return LessonDocument.builder()
            .moduleId(req.moduleId())
            .trackId(req.trackId())
            .title(req.title())
            .slug(req.slug())
            .sequence(req.sequence())
            .storyAnalogies(req.storyAnalogies())
            .starterCode(req.starterCode())
            .solutionTemplate(req.solutionTemplate())
            .testCases(req.testCases() != null ? req.testCases() : Collections.emptyList())
            .prerequisiteLessonId(req.prerequisiteLessonId())
            .createdAt(Instant.now())
            .build();
    }
}
