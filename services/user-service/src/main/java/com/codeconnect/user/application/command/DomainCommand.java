package com.codeconnect.user.application.command;

/**
 * Command contract encapsulating a transactional domain operation as a first-class object.
 * Follows the Command Pattern (GoF): enables queuing, audit logging, retryability, and undo.
 * Type parameter R is the result type produced upon execution.
 */
public interface DomainCommand<R> {

    R execute();
}
