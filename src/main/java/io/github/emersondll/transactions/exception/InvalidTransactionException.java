package io.github.emersondll.transactions.exception;

/**
 * Exception thrown when a transaction request contains invalid or missing data.
 *
 * <p>Mapped to HTTP 400 BAD REQUEST by the global exception handler.</p>
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
public class InvalidTransactionException extends RuntimeException {

    /**
     * Constructs the exception with the given detail message.
     *
     * @param message description of the validation failure
     */
    public InvalidTransactionException(String message) {
        super(message);
    }
}
