package com.codeconnect.submission.application.command;

/**
 * Fundamental Command Pattern contract for transactional operations.
 */
public interface DomainCommand<R> {
    R execute();
}
