package com.codeconnect.curriculum.presentation.controller;

import com.codeconnect.curriculum.application.dto.request.*;
import com.codeconnect.curriculum.application.dto.response.*;
import com.codeconnect.curriculum.application.service.AdminCurriculumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/curriculum")
@RequiredArgsConstructor
public class AdminCurriculumController {

    private final AdminCurriculumService adminCurriculumService;

    @PostMapping("/tracks")
    public ResponseEntity<ApiResponse<TrackResponse>> createTrack(@Valid @RequestBody CreateTrackRequest request) {
        TrackResponse response = adminCurriculumService.createTrack(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Track created successfully", response));
    }

    @PostMapping("/modules")
    public ResponseEntity<ApiResponse<ModuleResponse>> createModule(@Valid @RequestBody CreateModuleRequest request) {
        ModuleResponse response = adminCurriculumService.createModule(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Module created successfully", response));
    }

    @PostMapping("/lessons")
    public ResponseEntity<ApiResponse<LessonResponse>> createLesson(@Valid @RequestBody CreateLessonRequest request) {
        LessonResponse response = adminCurriculumService.createLesson(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Lesson created successfully", response));
    }

    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<ApiResponse<LessonResponse>> getLessonById(@PathVariable String lessonId) {
        LessonResponse response = adminCurriculumService.getLessonById(lessonId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
