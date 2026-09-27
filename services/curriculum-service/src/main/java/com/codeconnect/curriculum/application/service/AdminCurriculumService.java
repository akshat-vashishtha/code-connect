package com.codeconnect.curriculum.application.service;

import com.codeconnect.curriculum.application.dto.*;

public interface AdminCurriculumService {
    TrackResponse createTrack(CreateTrackRequest request);
    ModuleResponse createModule(CreateModuleRequest request);
    LessonResponse createLesson(CreateLessonRequest request);
    LessonResponse getLessonById(String lessonId);
}
