package com.codeconnect.curriculum.presentation.controller;

import com.codeconnect.curriculum.application.dto.*;
import com.codeconnect.curriculum.application.service.CurriculumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/curriculum")
@RequiredArgsConstructor
public class CurriculumController {

    private final CurriculumService curriculumService;

    @GetMapping("/tracks")
    public ResponseEntity<ApiResponse<List<TrackResponse>>> getPublishedTracks() {
        List<TrackResponse> tracks = curriculumService.getPublishedTracks();
        return ResponseEntity.ok(ApiResponse.success("Published tracks retrieved successfully", tracks));
    }

    @GetMapping("/tracks/{trackId}")
    public ResponseEntity<ApiResponse<TrackResponse>> getTrackById(@PathVariable String trackId) {
        TrackResponse track = curriculumService.getTrackById(trackId);
        return ResponseEntity.ok(ApiResponse.success("Track details retrieved successfully", track));
    }

    @GetMapping("/tracks/{trackId}/modules")
    public ResponseEntity<ApiResponse<List<ModuleResponse>>> getModulesByTrackId(@PathVariable String trackId) {
        List<ModuleResponse> modules = curriculumService.getModulesByTrackId(trackId);
        return ResponseEntity.ok(ApiResponse.success("Track modules retrieved successfully", modules));
    }

    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<ApiResponse<LessonResponse>> getLessonById(
        @PathVariable String lessonId,
        @RequestParam(required = false) String userId
    ) {
        LessonResponse lesson = curriculumService.getLessonById(lessonId, userId);
        return ResponseEntity.ok(ApiResponse.success("Lesson details retrieved successfully", lesson));
    }

    @GetMapping("/progress/{userId}/{trackId}")
    public ResponseEntity<ApiResponse<StudentProgressResponse>> getStudentProgress(
        @PathVariable String userId,
        @PathVariable String trackId
    ) {
        StudentProgressResponse progress = curriculumService.getStudentProgress(userId, trackId);
        return ResponseEntity.ok(ApiResponse.success("Student progress retrieved successfully", progress));
    }
}
