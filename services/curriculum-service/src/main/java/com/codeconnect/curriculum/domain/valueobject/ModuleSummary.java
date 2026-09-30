package com.codeconnect.curriculum.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Value summary representation of an embedded module within a TrackDocument.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleSummary {
    private String id;
    private String title;
    private String slug;
    private Integer sequence;
    private Integer lessonCount;
}

