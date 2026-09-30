package com.codeconnect.curriculum.application.command.impl;

import com.codeconnect.curriculum.application.command.CurriculumCommand;
import com.codeconnect.curriculum.application.dto.request.CreateTrackRequest;
import com.codeconnect.curriculum.application.dto.response.TrackResponse;
import com.codeconnect.curriculum.application.mapper.CurriculumMapper;
import com.codeconnect.curriculum.application.validator.CurriculumValidator;
import com.codeconnect.curriculum.domain.event.TrackCreatedEvent;
import com.codeconnect.curriculum.domain.model.TrackDocument;
import com.codeconnect.curriculum.domain.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;

/**
 * Command encapsulating Track creation workflow.
 * Follows the Command Pattern (GoF) and dispatches TrackCreatedEvent.
 */
@Slf4j
@RequiredArgsConstructor
public class CreateTrackCommand implements CurriculumCommand<TrackResponse> {

    private final CreateTrackRequest request;
    private final TrackRepository trackRepository;
    private final CurriculumValidator validator;
    private final CurriculumMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public TrackResponse execute() {
        log.info("Executing CreateTrackCommand for slug={}", request.slug());
        validator.validateTrackCreation(request);
        TrackDocument savedTrack = trackRepository.save(mapper.toTrackDocument(request));

        eventPublisher.publishEvent(new TrackCreatedEvent(
            savedTrack.getId(),
            savedTrack.getTitle(),
            savedTrack.getSlug(),
            savedTrack.getDescription(),
            savedTrack.getEstimatedHours(),
            savedTrack.getStatus(),
            savedTrack.getCreatedAt() != null ? savedTrack.getCreatedAt() : Instant.now()
        ));

        return mapper.toTrackResponse(savedTrack);
    }
}
