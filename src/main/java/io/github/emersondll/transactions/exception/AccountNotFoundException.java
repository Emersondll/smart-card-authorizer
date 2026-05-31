package io.github.emersondll.transactions.exception;

/**
 * Exception thrown when an account is not found by the given identifier.
 *
 * <p>Mapped to HTTP 404 NOT FOUND by the global exception handler.</p>
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
public class AccountNotFoundException extends RuntimeException {

    /**
     * Constructs the exception with the missing account ID.
     *
     * @param accountId the account identifier that was not found
     */
    public AccountNotFoundException(String accountId) {
        super("Account not found for id: " + accountId);
    }
}
