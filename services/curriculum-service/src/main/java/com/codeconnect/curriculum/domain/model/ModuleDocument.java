package com.codeconnect.curriculum.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Domain entity representing a curriculum module within a track.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "modules")
public class ModuleDocument {

    @Id
    private String id;
    private String trackId;
    private String title;
    private String slug;
    private Integer sequence;
    private String description;
    private String prerequisiteModuleId;
    private Instant createdAt;
}

