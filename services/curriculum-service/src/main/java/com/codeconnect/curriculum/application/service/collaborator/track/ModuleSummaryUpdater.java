package com.codeconnect.curriculum.application.service.collaborator.track;

import com.codeconnect.curriculum.domain.valueobject.ModuleSummary;
import com.codeconnect.curriculum.domain.model.TrackDocument;
import com.codeconnect.curriculum.domain.repository.LessonRepository;
import com.codeconnect.curriculum.domain.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Collaborator responsible for maintaining ModuleSummary denormalized state within TrackDocument.
 * Follows the Single Responsibility Principle: isolates all track-summary-mutation logic
 * out of the service layer into this focused component.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ModuleSummaryUpdater {

    private final TrackRepository trackRepository;
    private final LessonRepository lessonRepository;

    public void appendModuleSummary(TrackDocument track, ModuleSummary summary) {
        track.addOrUpdateModuleSummary(summary);
        trackRepository.save(track);
        log.debug("Appended module summary id={} to track id={}", summary.getId(), track.getId());
    }

    public void refreshLessonCount(String trackId, String moduleId) {
        trackRepository.findById(trackId).ifPresentOrElse(
            track -> updateModuleSummaryLessonCount(track, moduleId),
            () -> log.warn("Track id={} not found during lesson count refresh", trackId)
        );
    }

    private void updateModuleSummaryLessonCount(TrackDocument track, String moduleId) {
        int count = (int) lessonRepository.countByModuleId(moduleId);
        track.updateModuleLessonCount(moduleId, count);
        log.debug("Refreshed lesson count to {} for module id={}", count, moduleId);
        trackRepository.save(track);
    }
}
