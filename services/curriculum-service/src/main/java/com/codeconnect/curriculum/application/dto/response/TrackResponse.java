package com.codeconnect.curriculum.application.dto.response;

import com.codeconnect.curriculum.domain.enums.TrackStatus;
import com.codeconnect.curriculum.domain.valueobject.ModuleSummary;

import java.time.Instant;
import java.util.List;

public record TrackResponse(
    String id,
    String title,
    String slug,
    String description,
    Integer estimatedHours,
    TrackStatus status,
    List<ModuleSummary> modules,
    Instant createdAt
) {}
