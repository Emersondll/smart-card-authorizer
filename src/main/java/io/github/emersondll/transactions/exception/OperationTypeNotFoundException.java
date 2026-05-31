package io.github.emersondll.transactions.exception;

/**
 * Exception thrown when an operation type is not found by the given identifier.
 *
 * <p>Mapped to HTTP 404 NOT FOUND by the global exception handler.</p>
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
public class OperationTypeNotFoundException extends RuntimeException {

    /**
     * Constructs the exception with the missing operation type ID.
     *
     * @param operationTypeId the operation type identifier that was not found
     */
    public OperationTypeNotFoundException(String operationTypeId) {
        super("Operation type not found for id: " + operationTypeId);
    }
}
