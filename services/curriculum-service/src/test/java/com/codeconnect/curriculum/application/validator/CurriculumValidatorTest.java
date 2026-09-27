package com.codeconnect.curriculum.application.validator;

import com.codeconnect.curriculum.application.dto.CreateModuleRequest;
import com.codeconnect.curriculum.application.dto.CreateTrackRequest;
import com.codeconnect.curriculum.domain.enums.TrackStatus;
import com.codeconnect.curriculum.domain.exception.CurriculumValidationException;
import com.codeconnect.curriculum.domain.model.ModuleDocument;
import com.codeconnect.curriculum.domain.model.TrackDocument;
import com.codeconnect.curriculum.domain.repository.ModuleRepository;
import com.codeconnect.curriculum.domain.repository.TrackRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurriculumValidatorTest {

    @Mock
    private TrackRepository trackRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private CurriculumValidator validator;

    @Test
    @DisplayName("validateTrackCreation throws CurriculumValidationException when track slug already exists")
    void validateTrackCreation_DuplicateSlug_ShouldThrowException() {
        CreateTrackRequest request = new CreateTrackRequest(
            "Java Basics",
            "java-basics",
            "Description",
            10,
            TrackStatus.PUBLISHED
        );

        when(trackRepository.findBySlug("java-basics")).thenReturn(Optional.of(new TrackDocument()));

        assertThatThrownBy(() -> validator.validateTrackCreation(request))
            .isInstanceOf(CurriculumValidationException.class)
            .hasMessageContaining("Track slug already exists: java-basics");
    }

    @Test
    @DisplayName("validateModuleCreation throws CurriculumValidationException when parent track does not exist")
    void validateModuleCreation_MissingParentTrack_ShouldThrowException() {
        CreateModuleRequest request = new CreateModuleRequest(
            "invalid-track-id",
            "Module 1",
            "module-1",
            1,
            "Description",
            null
        );

        when(trackRepository.existsById("invalid-track-id")).thenReturn(false);

        assertThatThrownBy(() -> validator.validateModuleCreation(request))
            .isInstanceOf(CurriculumValidationException.class)
            .hasMessageContaining("Parent Track does not exist with ID: invalid-track-id");
    }
}
