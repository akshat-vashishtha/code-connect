package com.codeconnect.curriculum.application.validator;

import com.codeconnect.curriculum.application.dto.request.CreateLessonRequest;
import com.codeconnect.curriculum.application.dto.request.CreateModuleRequest;
import com.codeconnect.curriculum.application.dto.request.CreateTrackRequest;
import com.codeconnect.curriculum.domain.exception.CurriculumValidationException;
import com.codeconnect.curriculum.domain.repository.ModuleRepository;
import com.codeconnect.curriculum.domain.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurriculumValidator {

    private final TrackRepository trackRepository;
    private final ModuleRepository moduleRepository;

    public void validateTrackCreation(CreateTrackRequest request) {
        if (trackRepository.findBySlug(request.slug()).isPresent()) {
            throw new CurriculumValidationException("Track slug already exists: " + request.slug());
        }
    }

    public void validateModuleCreation(CreateModuleRequest request) {
        if (!trackRepository.existsById(request.trackId())) {
            throw new CurriculumValidationException("Parent Track does not exist with ID: " + request.trackId());
        }
        if (moduleRepository.findByTrackIdAndSlug(request.trackId(), request.slug()).isPresent()) {
            throw new CurriculumValidationException("Module slug already exists under this track: " + request.slug());
        }
    }

    public void validateLessonCreation(CreateLessonRequest request) {
        if (!moduleRepository.existsById(request.moduleId())) {
            throw new CurriculumValidationException("Parent Module does not exist with ID: " + request.moduleId());
        }
        if (!trackRepository.existsById(request.trackId())) {
            throw new CurriculumValidationException("Parent Track does not exist with ID: " + request.trackId());
        }
    }
}
