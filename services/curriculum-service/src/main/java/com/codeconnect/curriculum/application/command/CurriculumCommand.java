package com.codeconnect.curriculum.application.command;

/**
 * Functional interface encapsulating an executable curriculum transactional operation.
 * Follows the Command Pattern (GoF).
 */
@FunctionalInterface
public interface CurriculumCommand<T> {
    T execute();
}
