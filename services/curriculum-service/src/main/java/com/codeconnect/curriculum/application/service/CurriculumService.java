package com.codeconnect.curriculum.application.service;

import com.codeconnect.curriculum.application.dto.LessonResponse;
import com.codeconnect.curriculum.application.dto.ModuleResponse;
import com.codeconnect.curriculum.application.dto.StudentProgressResponse;
import com.codeconnect.curriculum.application.dto.TrackResponse;

import java.util.List;

public interface CurriculumService {
    List<TrackResponse> getPublishedTracks();
    TrackResponse getTrackById(String trackId);
    List<ModuleResponse> getModulesByTrackId(String trackId);
    LessonResponse getLessonById(String lessonId, String userId);
    StudentProgressResponse getStudentProgress(String userId, String trackId);
}
