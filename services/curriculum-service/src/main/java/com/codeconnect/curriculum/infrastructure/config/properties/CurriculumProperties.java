package com.codeconnect.curriculum.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Type-safe configuration properties for Curriculum Service.
 * Pure data holder — zero logic.
 */
@ConfigurationProperties(prefix = "codeconnect.curriculum")
public record CurriculumProperties(
    String defaultLanguage,
    Integer maxLessonsPerModule
) {}
